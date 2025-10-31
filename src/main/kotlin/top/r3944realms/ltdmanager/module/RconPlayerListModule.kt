package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.module.RconPlayerListModule.LastTriggerState
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownManager
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownScope
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownStateProvider
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.CooldownFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.KeywordFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.CmdUtil
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.util.concurrent.TimeoutException

class RconPlayerListModule(
    moduleName: String,
    private val groupMessagePollingModule: GroupMessagePollingModule,
    private val rconTimeOut: Long = 2_000L,
    private val cooldownMillis: Long = 30_000L,
    private val selfId: Long,
    private val selfNickName: String,
    private val rconPath: String,
    private val rconConfigPath: String,
    private val keywords: Set<String> = setOf("查看玩家列表", "玩家列表", "在线玩家")
) : BaseModule("RconPlayerListModule", moduleName), PersistentState<LastTriggerState> {
    private val cooldownManager by lazy {
        CooldownManager(
            cooldownMillis = cooldownMillis,
            scope = CooldownScope.Global,
            stateProvider = object : CooldownStateProvider<LastTriggerState> {
                override fun load() = loadState()
                override fun save(state: LastTriggerState) = saveState(state)
            },
            getLastTrigger = { state, _ -> state.lastTriggerTime to state.lastTriggeredRealId },
            updateTrigger = { state, _, realId, time ->
                // ✅ 消息成功触发时更新状态
                state.updateTrigger(realId, time)
                state
            },
            updateCooldownRealId = { state, _, realId ->
                // ✅ 消息被冷却拒绝时更新 lastCooldownRealId
                state.updateCooldownRealId(realId)
                state
            },
            groupId = groupMessagePollingModule.targetGroupId
        )
    }
    /** 抽象过滤器组合 —— lazy 避免初始化顺序问题 */
    private val triggerFilter by lazy {
        TriggerMessageFilter(
            listOf(
                IgnoreSelfFilter(selfId),
                NewMessageFilter { _ ->
                    lastTriggerState.lastTriggerTime to lastTriggerState.lastTriggeredRealId
                },
                KeywordFilter(keywords),
                CooldownFilter(cooldownManager) { msg, remain ->
                    sendCooldownMessage(napCatClient, msg.realId, remain)
                }
            )
        )
    }
    private var scope : CoroutineScope? = null

    // 持久化文件路径
    private val stateFile: File = getStateFileInternal("rcon_playerlist_state.json", name)

    private val stateBackupFile: File = getStateFileInternal("rcon_playerlist_state.json.bak", name)

    override fun getStateFileInternal(): File = stateFile

    // 保存最新触发过的消息 realId 和 time
    private var lastTriggerState: LastTriggerState = loadState()

    override fun getState(): LastTriggerState = lastTriggerState

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已装载，目标群组: ${groupMessagePollingModule.targetGroupId}")
        LoggerUtil.logger.info("[$name] 上次触发状态: realId=${lastTriggerState.lastTriggeredRealId}, time=${lastTriggerState.lastTriggerTime}")
        LoggerUtil.logger.info("[$name] 关键词列表: $keywords")
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 轮询协程启动")
            scope!!.launch {
                LoggerUtil.logger.info("[$name] 开始订阅消息流")
                groupMessagePollingModule.messagesFlow.collect { messages ->
                    if(loaded) handleMessages(messages)
                }
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载，取消协程...")
        scope?.cancel()
        saveState(lastTriggerState)
        LoggerUtil.logger.info("[$name] 模块已卸载完成")
    }

    private suspend fun handleMessages(messages: List<MsgHistorySpecificMsg>) {
        val filtered = triggerFilter.filter(messages)

        // RCON 模块只取最新的一条消息
        val triggerMsg = filtered.maxByOrNull { it.time }
        if (triggerMsg != null) {
            try {
                processTrigger(triggerMsg)
            } catch (e: Exception) {
                LoggerUtil.logger.error("[$name] 处理触发消息失败", e)
                sendFailedMessage(napCatClient, triggerMsg.realId, triggerMsg.time, "处理异常: ${e.message}")
            }
        }
    }
    private suspend fun processTrigger(msg: MsgHistorySpecificMsg) {
        LoggerUtil.logger.info("[$name] 执行 RCON 查询")

        val commands = listOf("forge tps", "list")
        LoggerUtil.logger.info("[$name] 执行 RCON 命令: $commands")

        runCatching {
            val tpsOutput = CmdUtil.runExeCommand(
                rconPath, "-c", rconConfigPath,
                "-T", (rconTimeOut / 1000).toString() + "s", "forge tps"
            )
            val listOutput = CmdUtil.runExeCommand(
                rconPath, "-c", rconConfigPath,
                "-T", (rconTimeOut / 1000).toString() + "s", "list"
            )

            if (tpsOutput.contains("i/o timeout") || listOutput.contains("i/o timeout")) {
                throw TimeoutException()
            }

            buildString {
                appendLine(tpsOutput.trim())
                appendLine("--------")
                appendLine(listOutput.trim())
            }
        }.onFailure { ex ->
            LoggerUtil.logger.error("[$name] RCON 查询失败", ex)
            if (ex is TimeoutException) {
                sendFailedMessage(napCatClient, msg.realId, msg.time, "⏳ RCON 连接超时")
                // ✅ 更新触发状态 & 持久化
                lastTriggerState.lastTriggeredRealId = msg.realId
                lastTriggerState.lastTriggerTime = msg.time
                saveState(lastTriggerState)
                return
            }
            throw ex
        }.onSuccess { output ->
            val tpsInfo = parseTPS(output)
            val playerListInfo = parsePlayerList(output)

            sendForwardMessage(napCatClient, tpsInfo, playerListInfo, msg.realId, msg.time)
        }

        // ✅ 更新触发状态 & 持久化
        lastTriggerState.lastTriggeredRealId = msg.realId
        lastTriggerState.lastTriggerTime = msg.time
        saveState(lastTriggerState)
    }


    private suspend fun sendCooldownMessage(client: NapCatClient, realId: Long, remaining: Long) {
        val msg = "⏳ 查询过于频繁，请稍后再试（剩余 $remaining 秒）"
        LoggerUtil.logger.info("[$name] 发送冷却提示: $msg")

        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(realId), msg),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        client.sendUnit(request)
    }

    private val failedMessages = listOf(
        "💥 土豆服务器炸了，请稍后再试",
        "🥔 土豆过热，正在冷却中……",
        "🐌 RCON 响应太慢，像蜗牛一样",
        "🛠️ 系统开小差了，请联系管理员",
        "⚠️ 服务器没理我，可能在打盹",
        "🔥 电路冒烟了！查询失败",

        // 新增的
        "⏳ 等了半天也没回应，土豆睡着了？",
        "📡 信号迷路了，RCON 连接失败",
        "🌀 数据转圈圈，一直出不来",
        "🚧 前方施工中，暂时无法获取数据",
        "🤖 RCON 小机器人宕机，请稍后重启",
        "🌩️ 网络打雷了，数据全跑丢了",
        "🕳️ 请求掉进黑洞了，没有回音",
        "🎭 服务器玩消失，不肯理我",
        "📉 查询失败，RCON 掉线了",
        "🥶 服务器结冰了，冻得说不出话",
        "📵 RCON 拒绝通信，像开飞行模式",
        "💤 服务器打瞌睡，回应超时"
    )
    private suspend fun sendFailedMessage(
        client: NapCatClient,
        realId: Long,
        time: Long,
        text: String? = null
    ) {
        // 如果调用时传了 text，就用 text，否则随机选择一条
        val finalText = text ?: failedMessages.random()

        LoggerUtil.logger.info("[$name] 发送失败消息: realId=$realId, text=$finalText")

        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(realId), finalText),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        client.sendUnit(request)
        LoggerUtil.logger.info("[$name] 已发送 RCON 失败消息")

        // 更新触发的最大 realId
        lastTriggerState.lastTriggeredRealId = realId
        lastTriggerState.lastTriggerTime = time
        saveState(lastTriggerState) // 保存到文件
    }
    private suspend fun sendForwardMessage(client: NapCatClient, tps: TPSInfo, info: PlayerListInfo, realId: Long, time: Long) {
        LoggerUtil.logger.info("[$name] 发送转发消息: realId=$realId, TPS=${tps.overall.meanTPS}, 在线玩家数=${info.onlineCount}")

        val messages = mutableListOf<SendForwardMsgRequest.Message>()

        // ① 服务器TPS状态
        val tpsMessage = SendForwardMsgRequest.Message(
            data = SendForwardMsgRequest.PurpleData(
                text = buildString {
                    appendLine("⚡ 服务器性能状态 ${getStatusEmoji(tps.status)}")
                    appendLine("═".repeat(25))
                    appendLine("整体TPS: ${"%.3f".format(tps.overall.meanTPS)} （${getStatusDescription(tps.status)}）")
                    appendLine("平均Tick耗时: ${"%.3f".format(tps.overall.meanTickTime)} ms")
                    appendLine()
                    appendLine("📌 各维度详情:")
                    tps.dimensions.forEach {
                        appendLine("- ${it.name}: ${"%.3f".format(it.meanTPS)} TPS, ${"%.3f".format(it.meanTickTime)} ms")
                    }
                }
            ),
            type = MessageType.Text
        )
        messages.add(tpsMessage)

        // ② 玩家列表
        if (info.players.isNotEmpty()) {
            val playerListMessage = SendForwardMsgRequest.Message(
                data = SendForwardMsgRequest.PurpleData(
                    text = buildString {
                        appendLine("👥 玩家列表")
                        appendLine("─".repeat(20))
                        appendLine("在线人数: ${info.onlineCount}")
                        info.players.forEachIndexed { index, player ->
                            appendLine("${index + 1}. 🧑‍💻 $player")
                        }
                    }
                ),
                type = MessageType.Text
            )
            messages.add(playerListMessage)
        } else {
            messages.add(
                SendForwardMsgRequest.Message(
                    data = SendForwardMsgRequest.PurpleData("😴 当前没有玩家在线\n"),
                    type = MessageType.Text
                )
            )
        }

        // ③ 摘要消息
        val summaryMessage = SendForwardMsgRequest.Message(
            data = SendForwardMsgRequest.PurpleData(
                text = buildString {
                    appendLine("📊 查询摘要")
                    appendLine("─".repeat(15))
                    appendLine("TPS: ${"%.3f".format(tps.overall.meanTPS)} - ${getStatusDescription(tps.status)}")
                    appendLine("在线玩家: ${info.onlineCount} 人")
                    appendLine("🕐 ${getCurrentTime()}")
                    appendLine("🤖 由 $selfNickName 提供")
                }
            ),
            type = MessageType.Text
        )
        messages.add(summaryMessage)

        val topMessage = SendForwardMsgRequest.TopForwardMsg(
            data = SendForwardMsgRequest.MessageData(
                content = messages,
                nickname = selfNickName,
                userId = ID.long(selfId),
            ),
            type = MessageType.Node
        )

        val request = SendForwardMsgRequest(
            groupId = ID.long(groupMessagePollingModule.targetGroupId),
            messages = listOf(topMessage),
            news = listOf(
                SendForwardMsgRequest.ForwardModelNews("点击查看服务器状态与玩家列表"),
                SendForwardMsgRequest.ForwardModelNews("TPS: ${"%.1f".format(tps.overall.meanTPS)} 在线 ${info.onlineCount} 人"),
                SendForwardMsgRequest.ForwardModelNews("更新时间: ${getCurrentTime()}")
            ),
            prompt = "TPS + 玩家列表查询结果",
            source = "🎮 服务器状态",
            summary = "TPS ${"%.1f".format(tps.overall.meanTPS)}, 在线玩家: ${info.onlineCount}人",
        )

        client.sendUnit(request)
        LoggerUtil.logger.info("[$name] 已发送 TPS+玩家列表 转发消息")
        lastTriggerState.lastTriggeredRealId = realId
        lastTriggerState.lastTriggerTime = time
        saveState(lastTriggerState)
    }

    // 添加时间格式化函数
    private fun getCurrentTime(): String {
        return java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
    }
// 在类内部添加以下数据类和函数

    @Serializable
    data class TPSInfo(
        val dimensions: List<DimensionTPS>,
        val overall: OverallTPS,
        val status: ServerStatus
    ) {
        @Serializable
        data class DimensionTPS(
            val name: String,
            val meanTickTime: Double,
            val meanTPS: Double
        )
        @Serializable
        data class OverallTPS(
            val meanTickTime: Double,
            val meanTPS: Double
        )

        enum class ServerStatus {
            EXCELLENT,    // TPS = 20.0
            GOOD,         // TPS >= 18.0
            FAIR,         // TPS >= 15.0
            POOR,         // TPS >= 10.0
            CRITICAL      // TPS < 10.0
        }
    }

    // 修改 parsePlayerList 函数来处理组合输出
    private fun parsePlayerList(output: String): PlayerListInfo {
        LoggerUtil.logger.debug("[$name] 解析玩家列表输出: ${output.take(100)}...")

        // 检查是否是连接超时错误
        if (output.contains("dial tcp") && output.contains("i/o timeout")) {
            LoggerUtil.logger.warn("[$name] 检测到连接超时错误")
            throw TimeoutException("服务器不可达")
        }

        // 分割输出，获取玩家列表部分
        val parts = output.split("--------")
        val playerListOutput = if (parts.size > 1) parts[1].trim() else output

        val regex = Regex("""There are (\d+) of a max of \d+ players online:\s*(.*)""")
        val match = regex.find(playerListOutput)

        if (match == null) {
            LoggerUtil.logger.warn("[$name] 无法解析玩家列表输出，返回空列表")
            return PlayerListInfo(0, emptyList())
        }

        val onlineCount = match.groupValues[1].toInt()
        val playersString = match.groupValues[2]

        val players = playersString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        LoggerUtil.logger.debug("[{}] 解析完成: 在线 {} 人，玩家列表: {}", name, onlineCount, players)
        return PlayerListInfo(onlineCount, players)
    }

    // 修改 parseTPS 函数来处理组合输出
    private fun parseTPS(output: String): TPSInfo {
        LoggerUtil.logger.debug("[$name] 解析TPS输出: ${output.take(100)}...")

        // 分割输出，获取TPS部分
        val parts = output.split("--------")
        val tpsOutput = parts[0].trim()

        val dimensionRegex = Regex("""Dim (.+?): Mean tick time: (\d+\.\d+) ms\. Mean TPS: (\d+\.\d+)""")
        val overallRegex = Regex("""Overall: Mean tick time: (\d+\.\d+) ms\. Mean TPS: (\d+\.\d+)""")

        val dimensions = mutableListOf<TPSInfo.DimensionTPS>()
        var overall: TPSInfo.OverallTPS? = null

        tpsOutput.lineSequence().forEach { line ->
            // 解析维度TPS
            dimensionRegex.find(line)?.let { match ->
                val name = match.groupValues[1]
                val meanTickTime = match.groupValues[2].toDouble()
                val meanTPS = match.groupValues[3].toDouble()

                dimensions.add(TPSInfo.DimensionTPS(name, meanTickTime, meanTPS))
            }

            // 解析总体TPS
            overallRegex.find(line)?.let { match ->
                val meanTickTime = match.groupValues[1].toDouble()
                val meanTPS = match.groupValues[2].toDouble()

                overall = TPSInfo.OverallTPS(meanTickTime, meanTPS)
            }
        }

        if (overall == null) {
            throw IllegalArgumentException("无法解析TPS输出: $output")
        }

        // 确定服务器状态
        val status = when (overall!!.meanTPS) {
            20.0 -> TPSInfo.ServerStatus.EXCELLENT
            in 18.0..19.99 -> TPSInfo.ServerStatus.GOOD
            in 15.0..17.99 -> TPSInfo.ServerStatus.FAIR
            in 10.0..14.99 -> TPSInfo.ServerStatus.POOR
            else -> TPSInfo.ServerStatus.CRITICAL
        }

        return TPSInfo(dimensions, overall!!, status)
    }

    // 获取服务器状态表情符号
    private fun getStatusEmoji(status: TPSInfo.ServerStatus): String {
        return when (status) {
            TPSInfo.ServerStatus.EXCELLENT -> "💚" // 绿色心形，优秀
            TPSInfo.ServerStatus.GOOD -> "💛"      // 黄色心形，良好
            TPSInfo.ServerStatus.FAIR -> "🟡"      // 黄色圆形，一般
            TPSInfo.ServerStatus.POOR -> "🟠"      // 橙色圆形，较差
            TPSInfo.ServerStatus.CRITICAL -> "🔴"  // 红色圆形，严重
        }
    }

    // 获取服务器状态描述
    private fun getStatusDescription(status: TPSInfo.ServerStatus): String {
        return when (status) {
            TPSInfo.ServerStatus.EXCELLENT -> "优秀"
            TPSInfo.ServerStatus.GOOD -> "良好"
            TPSInfo.ServerStatus.FAIR -> "一般"
            TPSInfo.ServerStatus.POOR -> "较差"
            TPSInfo.ServerStatus.CRITICAL -> "严重"
        }
    }
    data class PlayerListInfo(
        val onlineCount: Int,
        val players: List<String>
    )



    // ---------------- 持久化部分 ----------------

    @Serializable
    data class LastTriggerState(
        var lastTriggeredRealId: Long = -1,     // 上次允许处理消息ID
        var lastTriggerTime: Long = 0,          // 上次允许处理时间（毫秒或秒都可以，根据你的逻辑）
        var lastCooldownRealId: Long = -1       // 上次冷却期间被拒绝的消息ID
    ) {
        /** ✅ 冷却结束，更新触发状态 */
        fun updateTrigger(realId: Long, time: Long) {
            lastTriggeredRealId = realId
            lastTriggerTime = time
            // 保留 lastCooldownRealId 不变
        }

        /** ⚠️ 冷却中，更新冷却消息ID */
        fun updateCooldownRealId(realId: Long) {
            lastCooldownRealId = realId
            // 保留 lastTriggeredRealId 和 lastTriggerTime
        }
    }

    override fun saveState(state: LastTriggerState) {
        try {
            // 先备份现有主文件
            if (stateFile.exists()) {
                stateFile.copyTo(stateBackupFile, overwrite = true)
            }

            // 写入主文件
            stateFile.writeText(Json.encodeToString(state))
            LoggerUtil.logger.info("[$name] 已保存状态: lastTriggeredRealId=${state.lastTriggeredRealId}, lastTriggerTime=${state.lastTriggerTime}")
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 保存状态失败", e)
        }
    }

    override fun loadState(): LastTriggerState {
        return try {
            val fileToRead = when {
                stateFile.exists() -> stateFile
                stateBackupFile.exists() -> stateBackupFile
                else -> null
            }

            if (fileToRead == null) {
                LoggerUtil.logger.info("[$name] 状态文件不存在，使用默认值")
                return LastTriggerState(-1L, 0L)
            }

            val state = Json.decodeFromString<LastTriggerState>(fileToRead.readText())
            LoggerUtil.logger.info("[$name] 成功加载状态: lastTriggeredRealId=${state.lastTriggeredRealId}, lastTriggerTime=${state.lastTriggerTime}")
            state
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 读取状态失败，使用默认值", e)
            LastTriggerState(-1L, 0L)
        }
    }
    // 返回模块基本信息
    override fun info(): String = buildString {
        appendLine("模块名称: $name")
        appendLine("模块类型: RconPlayerListModule")
        appendLine("目标群组: ${groupMessagePollingModule.targetGroupId}")
        appendLine("机器人昵称: $selfNickName (ID: $selfId)")
        appendLine("冷却时间: ${cooldownMillis / 1000} 秒")
        appendLine("RCON 命令路径: $rconPath")
        appendLine("RCON 配置文件路径: $rconConfigPath")
        appendLine("RCON 超时时间: $rconTimeOut ms")
        appendLine("关键词触发: ${keywords.joinToString(", ")}")
        appendLine("状态文件路径: ${stateFile.absolutePath}")
        appendLine("状态备份文件路径: ${stateBackupFile.absolutePath}")
        appendLine("上次触发消息ID: ${lastTriggerState.lastTriggeredRealId}")
        appendLine("上次触发时间: ${lastTriggerState.lastTriggerTime}")
    }

    // 返回模块使用帮助
    override fun help(): String = buildString {
        appendLine("使用帮助 - RconPlayerListModule")
        appendLine("功能: 查询服务器 TPS 和在线玩家列表，通过关键词触发或冷却机制限制频率")
        appendLine("触发关键词: ${keywords.joinToString(", ")}")
        appendLine("示例:")
        keywords.forEach { keyword ->
            appendLine("  - 在群里发送 \"$keyword\" 将触发 RCON 查询")
        }
        appendLine("注意事项:")
        appendLine("  - 查询冷却时间为 ${cooldownMillis / 1000} 秒")
        appendLine("  - RCON 查询可能受服务器响应时间影响")
        appendLine("  - 查询结果会以转发消息形式发送到群组")
    }

}