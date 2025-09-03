package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.mcserver.McServerStatus
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class McServerStatusModule(
    private val groupMessagePollingModule: GroupMessagePollingModule,
    private val selfId: Long,
    private val selfNickName: String,
    private val cooldownSeconds: Long = 60,
    private val commands: List<String> = listOf("/mcs", "/s"),
    private val presetServer: Map<Set<String>, String> = mapOf(
        setOf("hp", "hypixel") to "mc.hypixel.net",
        setOf("pm", "mineplex") to "play.mineplex.com"
    )
) : BaseModule(), PersistentState<McServerStatusModule.CooldownState> {
    private val presetServerByAlias: Map<String, String> by lazy {
        presetServer.flatMap { (aliases, ip) ->
            aliases.map { it.lowercase() to ip }
        }.toMap()
    }
    fun getServerIp(alias: String): String? = presetServerByAlias[alias.lowercase()]
    override val name: String = "McServerStatusModule"
    private var scope: CoroutineScope? = null
    private val stateFile = getStateFile("mc_server_status_state.json")
    private val stateBackupFile = getStateFile("mc_server_status_state.json.bak")
    private val fileLock = ReentrantLock()
    private var cooldownState = loadState()

    override fun getStateFile(): File = stateFile
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

    private suspend fun handleMessages(messages: List<GetFriendMsgHistoryEvent.SpecificMsg>) {
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
    private suspend fun filterTriggerMessages(messages: List<GetFriendMsgHistoryEvent.SpecificMsg>)
            : List<GetFriendMsgHistoryEvent.SpecificMsg> {

        val filtered = messages.asSequence()
            .filter { msg ->
                // 忽略自己消息
                msg.userId != selfId &&
                        // 新消息判断
                        (msg.time > cooldownState.getLastTriggerTime(msg.userId) ||
                                (msg.time == cooldownState.getLastTriggerTime(msg.userId) &&
                                        msg.realId > cooldownState.getLastTriggerRealId(msg.userId)))
            }
            .filter { msg ->
                // 检查命令
                msg.message.any { seg ->
                    seg.type == MessageType.Text &&
                            (
                                    seg.data.text?.let { text -> commands.any { cmd -> text.startsWith(cmd) } } == true
                            )
                }
            }
            .filter { runBlocking { handleCooldown(it) } } // 这里处理冷却
            .toList()

        return filtered
    }
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
    /** 冷却提示消息 */

   private suspend fun handleCooldown(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        val trigger = cooldownState.map[msg.userId]
        val lastTriggerTime = trigger?.time ?: -1L
        val lastCooldownRealId = trigger?.lastCooldownRealId ?: -1L
        val nowSec = System.currentTimeMillis() / 1000

        // 未触发过或者已超过冷却
        if (lastTriggerTime == -1L || nowSec - lastTriggerTime >= cooldownSeconds) {
            return true
        }

        // 冷却中且未发送过冷却提示
        if (msg.realId != lastCooldownRealId) {
            val remaining = ((cooldownSeconds - (nowSec - lastTriggerTime))).coerceAtLeast(1)
            val msgText = "⏳ 查询过于频繁， $remaining 秒后执行查询，切勿重复发送"
            sendCooldownMessage(napCatClient, msg.userId, msg.realId, msgText)
            cooldownState = cooldownState.updateLastCooldownRealId(msg.userId, msg.realId)
        }

        return false
    }

    private suspend fun sendCooldownMessage(client: NapCatClient, qq: Long, realId: Long, text: String) {
        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(realId), text),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        client.sendUnit(request)
    }



    private suspend fun processCommand(msg: GetFriendMsgHistoryEvent.SpecificMsg) {
        // 找出文本内容
        val text = msg.message
            .firstOrNull { it.type == MessageType.Text }
            ?.data?.text
            ?.trim()
            ?: return

        // 解析命令
        val matchedCommand = commands.firstOrNull { text.startsWith(it) } ?: return
        var address = text.removePrefix(matchedCommand).trim()

        // 使用预设别名替换
        presetServerByAlias[address.lowercase()]?.let { presetIp ->
            address = presetIp
        }

        if (address.isEmpty()) {
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
            val status = mcSrvStatusClient.getServerStatus(address) // 返回 McServerStatus

            // 检查是否查询失败
            if (!status.online) {
                sendFailedMessage(
                    napCatClient, msg.userId, msg.realId, msg.time,
                    "❌ 查询失败，请检查服务器地址或服务器是否在线"
                )
                return
            }

            // 查询成功，发送状态消息
            sendStatusForwardMessage(napCatClient, msg, address, status, msg.realId, msg.time)

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
        msg: GetFriendMsgHistoryEvent.SpecificMsg,
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
        fun getLastTriggerTime(qq: Long): Long = map[qq]?.time ?: -1
        fun getLastTriggerRealId(qq: Long): Long = map[qq]?.realId ?: -1
        fun updateLastTrigger(qq: Long, realId: Long, time: Long = -1): CooldownState {
            val old = map[qq]
            val newTime = if (time != -1L) time else old?.time ?: -1
            val newMap = map.toMutableMap().apply {
                put(qq, TriggerDetail(realId, newTime, old?.lastCooldownRealId ?: -1))
            }
            return copy(map = newMap)
        }
        fun updateLastCooldownRealId(qq: Long, realId: Long): CooldownState {
            val old = map[qq]
            val newMap = map.toMutableMap().apply {
                put(qq, TriggerDetail(
                    realId = old?.realId ?: -1,
                    time = old?.time ?: -1,
                    lastCooldownRealId = realId
                ))
            }
            return copy(map = newMap)
        }
    }

    @Serializable
    data class TriggerDetail(
        val realId: Long,
        val time: Long,
        val lastCooldownRealId: Long = -1L
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
}
