package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.mcserver.McServerStatus
import top.r3944realms.ltdmanager.module.common.CommandParser
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownManager
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownScope
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownStateProvider
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.CommandFilter
import top.r3944realms.ltdmanager.module.common.filter.type.CooldownFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class McServerStatusModule(
    moduleName: String,
    private val groupMessagePollingModule: GroupMessagePollingModule,
    private val selfId: Long,
    private val selfNickName: String,
    private val cooldownMillis: Long = 60_000L,
    private val commands: List<String> = listOf("/mcs", "/s"),
    private val presetServer: Map<Set<String>, String> = mapOf(
        setOf("hp", "hypixel") to "mc.hypixel.net",
        setOf("pm", "mineplex") to "play.mineplex.com"
    )
) : BaseModule("McServerStatusModule", moduleName), PersistentState<McServerStatusModule.CooldownState> {
    private val stateFile:File = getStateFileInternal("mc_server_status_state.json", name)
    private val stateBackupFile:File = getStateFileInternal("mc_server_status_state.json.bak", name)
    private val commandParser: CommandParser = CommandParser(commands)

    private val cooldownManager by lazy {
        CooldownManager(
            cooldownMillis = cooldownMillis,
            scope = CooldownScope.PerUser,
            stateProvider = object : CooldownStateProvider<CooldownState> {
                override fun load() = loadState()
                override fun save(state: CooldownState) = saveState(state)
            },
            getLastTrigger = { state, qq ->
                val detail = state.map[qq]
                (detail?.time ?: -1L) to (detail?.lastCooldownRealId ?: -1L)
            },
            updateTrigger = { state, qq, realId, time ->
                val id = requireNotNull(qq) { "userId required for per-user cooldown" }
                state.updateLastTrigger(id, realId, time) }
            ,
            updateCooldownRealId = { state, qq, realId ->
                val id = requireNotNull(qq) { "userId required for per-user cooldown" }
                state.updateLastCooldownRealId(id, realId)
            },
            groupId = groupMessagePollingModule.targetGroupId
        )
    }
    private val triggerFilter = TriggerMessageFilter(
        listOf(
            IgnoreSelfFilter(selfId),
            NewMessageFilter { qq ->
                cooldownState.getLastTriggerTime(qq) to cooldownState.getLastTriggerRealId(qq)
            },
            CommandFilter(commandParser),
            CooldownFilter(
                cooldownManager = cooldownManager,
                sendCooldown = { msg, remaining ->
                    sendCooldownMessage(napCatClient, msg.realId, "⏳ 查询过于频繁， $remaining 秒后执行查询，切勿重复发送")
                }
            )
        )
    )

    private val presetServerByAlias: Map<String, String> by lazy {
        presetServer.flatMap { (aliases, ip) ->
            aliases.map { it.lowercase() to ip }
        }.toMap()
    }
    fun getServerIp(alias: String): String? = presetServerByAlias[alias.lowercase()]
    private var scope: CoroutineScope? = null

    private val fileLock = ReentrantLock()
    private var cooldownState = loadState()

    override fun getStateFileInternal(): File = stateFile
    override fun getState(): CooldownState = cooldownState

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已装载，目标群组: ${groupMessagePollingModule.targetGroupId}")

        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 轮询协程启动")
            groupMessagePollingModule.messagesFlow.collect { messages ->
                if (loaded) handleMessages(messages)
            }
        }
    }

    override suspend fun onUnload() {
        saveState(cooldownState)
        scope?.cancel()
        LoggerUtil.logger.info("[$name] 模块已卸载完成")
    }

    private suspend fun handleMessages(messages: List<MsgHistorySpecificMsg>) {
        if (messages.isEmpty()) return
        val triggerMsgs = filterTriggerMessages(messages)
        if (triggerMsgs.isEmpty()) return

        try {
            triggerMsgs.forEach {
                processCommand(it)
            }
        } catch (e: Exception) {
            sendFailedMessage(napCatClient, text = "系统错误，请联系管理员: $e")
        } finally {
            saveState(cooldownState)
        }
    }

    private suspend fun filterTriggerMessages(
        messages: List<MsgHistorySpecificMsg>
    ): List<MsgHistorySpecificMsg> = triggerFilter.filter(messages)

    private suspend fun sendFailedMessage(
        client: NapCatClient,
        qq: Long? = null,
        realId: Long? = null,
        time: Long? = null,
        text: String = "失败消息"
    ) {
        LoggerUtil.logger.info("[$name] 发送失败消息: realId=$realId, text=$text")
        if (realId != null && qq != null && time != null) {
            val request = SendGroupMsgRequest(
                MessageElement.reply(ID.long(realId), text),
                ID.long(groupMessagePollingModule.targetGroupId)
            )
            client.sendUnit(request)
            LoggerUtil.logger.info("[$name] 已发送 失败消息")

            // 更新触发的最大 realId
            cooldownState = cooldownState.updateLastTrigger(qq, realId, time)
        } else {
            val request = SendGroupMsgRequest(
                listOf(MessageElement.text(text)),
                ID.long(groupMessagePollingModule.targetGroupId)
            )
            client.sendUnit(request)
            LoggerUtil.logger.info("[$name] 已发送 失败消息[无指定对象]")
        }
    }
    private suspend fun sendCooldownMessage(client: NapCatClient, realId: Long, text: String) {
        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(realId), text),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        client.sendUnit(request)
    }



    private suspend fun processCommand(msg: MsgHistorySpecificMsg) {
        // 找出文本内容
        val text = msg.message
            .firstOrNull { it.type == MessageType.Text }
            ?.data?.text
            ?.trim()
            ?: return

        // 使用命令解析器解析命令
        val parsedCommand = commandParser.parseCommand(text) ?: return
        val (_, address) = parsedCommand

        // 使用预设别名替换
        val finalAddress = if (address.isNotEmpty()) {
            presetServerByAlias[address.lowercase()] ?: address
        } else {
            ""
        }

        if (finalAddress.isEmpty()) {
            sendFailedMessage(
                napCatClient,
                msg.userId,
                msg.realId,
                msg.time,
                "❌ 请输入服务器地址，例如 /mcs n2.akiracloud.net:10599"
            )
            return
        }

        try {
            val status = mcSrvStatusClient.getServerStatus(finalAddress)

            if (!status.online) {
                sendFailedMessage(
                    napCatClient, msg.userId, msg.realId, msg.time,
                    "❌ 查询失败，请检查服务器地址或服务器是否在线"
                )
                return
            }

            sendStatusForwardMessage(napCatClient, msg, finalAddress, status, msg.realId, msg.time)
        } catch (e: Exception) {
            LoggerUtil.logger.error("查询服务器状态失败: $address", e)
            sendFailedMessage(
                napCatClient,
                msg.userId,
                msg.realId,
                msg.time,
                "❌ 查询失败，请检查服务器地址或服务器是否在线"
            )
        }
    }

    // ---------------- 转发消息封装 ----------------
    private suspend fun sendStatusForwardMessage(
        client: NapCatClient,
        msg: MsgHistorySpecificMsg,
        address: String,
        status: McServerStatus,
        realId: Long,
        time: Long
    ) {
        LoggerUtil.logger.info("[$name] 发送服务器状态转发消息: realId=$realId, address=$address, online=${status.online}")

        val messages = mutableListOf<SendForwardMsgRequest.Message>()

        // ① 服务器基本信息 + MOTD
        val motdText = status.motd?.clean?.joinToString("\n") ?: "无 MOTD"
        val basicInfo = buildString {
            appendLine("🌐 服务器: $address")
            appendLine("─".repeat(25))
            appendLine("MOTD:\n$motdText")
        }
        messages.add(SendForwardMsgRequest.Message(SendForwardMsgRequest.PurpleData(basicInfo), MessageType.Text))

        // ② 玩家列表
        val playerList = status.players?.list?.joinToString("\n") { it.name } ?: "无"
        val playersInfo = buildString {
            appendLine("📊 在线: ${status.players?.online ?: 0}/${status.players?.max ?: 0}")
            appendLine("👥 玩家:\n$playerList")
        }
        messages.add(SendForwardMsgRequest.Message(SendForwardMsgRequest.PurpleData(playersInfo), MessageType.Text))

        // ③ 版本 + 状态
        val versionStatus = buildString {
            appendLine("🎮 版本: ${status.version ?: "未知"}")
            appendLine("✅ 状态: ${if (status.online) "在线" else "离线"}")
            status.software?.let { appendLine("💻 软件: $it") }
        }
        messages.add(SendForwardMsgRequest.Message(SendForwardMsgRequest.PurpleData(versionStatus), MessageType.Text))

        // ④ 摘要信息
        val summaryText = buildString {
            appendLine("📌 查询摘要")
            appendLine("─".repeat(20))
            appendLine("服务器: $address")
            appendLine("在线玩家: ${status.players?.online ?: 0}/${status.players?.max ?: 0}")
            appendLine("状态: ${if (status.online) "在线" else "离线"}")
            appendLine("🕐 ${getCurrentTime()}")
            appendLine("🤖 由 $selfNickName 提供")
        }
        messages.add(SendForwardMsgRequest.Message(SendForwardMsgRequest.PurpleData(summaryText), MessageType.Text))

        // 封装 Forward 消息
        val topMessage = SendForwardMsgRequest.TopForwardMsg(
            data = SendForwardMsgRequest.MessageData(
                content = messages,
                nickname = selfNickName,
                userId = ID.long(selfId)
            ),
            type = MessageType.Node
        )

        val request = SendForwardMsgRequest(
            groupId = ID.long(groupMessagePollingModule.targetGroupId),
            messages = listOf(topMessage),
            news = listOf(
                SendForwardMsgRequest.ForwardModelNews("点击查看服务器状态与玩家列表"),
                SendForwardMsgRequest.ForwardModelNews("在线 ${status.players?.online ?: 0} / ${status.players?.max ?: 0}"),
                SendForwardMsgRequest.ForwardModelNews("更新时间: ${getCurrentTime()}")
            ),
            prompt = "服务器状态查询结果",
            source = "🎮 服务器状态",
            summary = "在线 ${status.players?.online ?: 0} / ${status.players?.max ?: 0} 人"
        )

        client.sendUnit(request)
        LoggerUtil.logger.info("[$name] 已发送服务器状态转发消息")

        // 更新冷却状态
        cooldownState = cooldownState.updateLastTrigger(msg.userId, realId, time)
    }



    // 时间格式化
    private fun getCurrentTime(): String {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    // ---------------- 状态持久化 ----------------
    @Serializable
    data class CooldownState(
        val map: Map<Long, TriggerDetail> = emptyMap()
    ) {
        // 获取上次处理时间
        fun getLastTriggerTime(qq: Long): Long = map[qq]?.time ?: -1

        // 获取上次处理消息ID
        fun getLastTriggerRealId(qq: Long): Long = map[qq]?.realId ?: -1

        // 获取上次冷却消息ID
        fun getLastCooldownRealId(qq: Long): Long = map[qq]?.lastCooldownRealId ?: -1

        // 冷却结束，允许处理消息 → 更新 time 和 realId
        fun updateLastTrigger(qq: Long, realId: Long, time: Long): CooldownState {
            val old = map[qq]
            val newMap = map.toMutableMap().apply {
                put(qq, TriggerDetail(
                    realId = realId,                       // 当前允许处理消息ID
                    time = time,                           // 当前允许处理消息时间
                    lastCooldownRealId = old?.lastCooldownRealId ?: -1 // 保留冷却中记录的消息ID
                ))
            }
            return copy(map = newMap)
        }

        // 冷却中消息 → 只更新 lastCooldownRealId，保留 time 和 realId
        fun updateLastCooldownRealId(qq: Long, realId: Long): CooldownState {
            val old = map[qq]
            val newMap = map.toMutableMap().apply {
                put(qq, TriggerDetail(
                    realId = old?.realId ?: -1,           // 保持上次允许处理的消息ID
                    time = old?.time ?: -1,               // 保持上次允许处理的时间
                    lastCooldownRealId = realId           // 更新当前冷却拒绝的消息ID
                ))
            }
            return copy(map = newMap)
        }
    }

    @Serializable
    data class TriggerDetail(
        val realId: Long,             // 上次允许处理消息ID
        val time: Long,               // 上次允许处理消息时间（秒）
        val lastCooldownRealId: Long = -1 // 上次被冷却拒绝的消息ID
    )

    override fun loadState(): CooldownState {
        return try {
            val fileToRead = when {
                stateFile.exists() -> stateFile
                stateBackupFile.exists() -> stateBackupFile
                else -> null
            }
            if (fileToRead == null) return CooldownState()
            val content = fileToRead.readText()
            Json.decodeFromString(CooldownState.serializer(), content)
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 状态恢复失败，使用默认值", e)
            CooldownState()
        }
    }

    override fun saveState(state: CooldownState) {
        fileLock.withLock {
            try {
                val json = Json.encodeToString(CooldownState.serializer(), state)
                if (stateFile.exists()) stateFile.copyTo(stateBackupFile, overwrite = true)
                stateFile.writeText(json)
            } catch (e: Exception) {
                LoggerUtil.logger.error("[$name] 保存状态失败", e)
            }
        }
    }
    override fun info(): String {
        return buildString {
            appendLine("模块名称: $name")
            appendLine("模块类型: McServerStatusModule")
            appendLine("目标群组: ${groupMessagePollingModule.targetGroupId}")
            appendLine("机器人昵称: $selfNickName (ID: $selfId)")
            appendLine("冷却时间: ${cooldownMillis / 1000} 秒")
            appendLine("支持命令: ${commands.joinToString(", ")}")
            appendLine("预设服务器别名:")
            presetServer.forEach { (aliases, ip) ->
                appendLine("  ${aliases.joinToString("/")} -> $ip")
            }
            appendLine("状态文件路径: ${stateFile.absolutePath}")
            appendLine("状态备份文件路径: ${stateBackupFile.absolutePath}")
        }
    }
    // 返回模块使用帮助
    override fun help(): String = buildString {
        appendLine("使用帮助 - McServerStatusModule")
        appendLine("指令格式: /mcs <服务器别名或IP> 或 /s <服务器别名或IP>")
        appendLine("示例:")
        presetServerByAlias.forEach { (alias, ip) ->
            appendLine("  /mcs $alias -> 查询服务器 $ip 状态")
        }
        appendLine("注意事项:")
        appendLine("  - 查询冷却时间为 ${cooldownMillis / 1000} 秒")
        appendLine("  - 输入服务器 IP 或别名均可")
        appendLine("  - 查询结果会以转发消息形式发送到群组")
    }
}
