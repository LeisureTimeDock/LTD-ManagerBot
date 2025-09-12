package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.module.common.CommandParser
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.CommandFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent
import top.r3944realms.ltdmanager.napcat.request.group.SetGroupBanRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import kotlin.random.Random

/**
 * 指令触发禁言模块
 */
class CommandBanModule(
    moduleName: String,
    private val groupMessagePollingModule : GroupMessagePollingModule,
    private val selfId: Long,
    commandPrefixList: List<String> = listOf("/mute"), // 默认命令前缀
    private val minBanMinutes: Int = 1,
    private val maxBanMinutes: Int = 15
) : BaseModule("CommandBanModule", moduleName), PersistentState<CommandBanModule.BanState> {

    private val commandParser = CommandParser(commandPrefixList)
    private val commandFilter = CommandFilter(commandParser)
    private val banState = loadState()
    override fun getState(): BanState = banState

    private val triggerFilter by lazy {
        TriggerMessageFilter(
            listOf(
                IgnoreSelfFilter(selfId),
                NewMessageFilter { _ -> banState.lastTriggerTime to banState.lastTriggerRealId },
                commandFilter
            )
        )
    }

    private var scope: CoroutineScope? = null
    private val stateFile: File = getStateFileInternal("command_ban_state.json", name)
    private val stateBackupFile: File = getStateFileInternal("command_ban_state.json.bak", name)

    override fun getStateFileInternal(): File = stateFile

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已装载，监听群组: ${groupMessagePollingModule.targetGroupId}")
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 启动消息监听协程")
            groupMessagePollingModule.messagesFlow.collect { messages ->
                handleMessages(messages)
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载，取消协程")
        scope?.cancel()
    }

    private suspend fun handleMessages(messages: List<GetFriendMsgHistoryEvent.SpecificMsg>) {
        // 先过一遍过滤器，只有符合条件的才进入后续处理
        val filtered = triggerFilter.filter(messages)
        for (msg in filtered) {
            processBanCommand(msg)
        }
    }
    /**
     * 将 SpecificMsg 中的 message 段拼成一条“可解析文本”。
     * - text 段直接拼接
     * - 如果消息段里包含 @（在 MessageData 中为 qq 字段），则拼成 "@{qq}"，方便 parseMentionToUserId 解析
     */
    private fun GetFriendMsgHistoryEvent.SpecificMsg.plainText(): String {
        return this.message.joinToString(" ") { seg ->
            // 如果 message element 包含 qq 字段（即@用户），优先使用它
            seg.data.qq?.let { "@${it}" } ?: (seg.data.text ?: "")
        }.trim()
    }
    private suspend fun processBanCommand(msg: GetFriendMsgHistoryEvent.SpecificMsg) {
        try {
            val parsed = commandParser.parseCommand(msg.plainText()) ?: return
            val (command, argument) = parsed

            // 参数格式： [分钟]
            // 示例：/mute 5  → 自己禁言 5 分钟
            //       /mute    → 自己随机禁言
            val parts = argument.split(" ").filter { it.isNotBlank() }

            val durationMinutes = parts.getOrNull(0)?.toIntOrNull()
                ?: Random.nextInt(minBanMinutes, maxBanMinutes + 1)
            val durationSeconds = durationMinutes.coerceIn(minBanMinutes, maxBanMinutes) * 60

            val targetUserId = msg.sender.userId

            banUser(targetUserId, groupMessagePollingModule.targetGroupId, durationSeconds)
            sendGroupMessage("✅ 你已被禁言 $durationMinutes 分钟", msg.realId)

            // 更新状态（保证状态保存正确）
            banState.lastTriggerRealId = msg.realId
            banState.lastTriggerTime = msg.time
            saveState(banState)

        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 执行禁言指令失败", e)
            sendGroupMessage("❌ 执行禁言失败，请检查指令格式或权限", msg.realId)
        }
    }
    private suspend fun banUser(userId: Long, groupId: Long, seconds: Int) {
        val request = SetGroupBanRequest(
            duration = seconds.toDouble(),
            groupId = ID.long(groupId),
            userId = ID.long(userId)
        )
        napCatClient.sendUnit(request)
        LoggerUtil.logger.info("[$name] 已对用户 $userId 执行 $seconds 秒禁言")
    }

    private suspend fun sendGroupMessage(text: String, replyTo: Long? = null) {
        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(replyTo ?: 0), text),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        napCatClient.sendUnit(request)
    }

    override fun info(): String {
        return "[$name] 指令禁言模块：用户发送 ${commandParser.getCommands().joinToString("、")} 来禁言自己，" +
                "支持指定分钟数或随机分钟数，范围 $minBanMinutes-$maxBanMinutes 分钟。"
    }

    override fun help(): String {
        return buildString {
            appendLine("📖 [$name] 使用帮助：")
            appendLine(" - ${commandParser.getCommands().joinToString("、")} [分钟]")
            appendLine("   · 不写分钟数 → 随机禁言 (范围 $minBanMinutes-$maxBanMinutes 分钟)")
            appendLine("   · 写分钟数 → 自己禁言指定分钟数")
            appendLine()
            appendLine("示例：")
            appendLine(" - /mute       → 随机禁言自己")
            appendLine(" - /mute 5     → 禁言自己 5 分钟")
        }
    }

    // ---------------- 持久化 ----------------
    @Serializable
    data class BanState(
        var lastTriggerRealId: Long = -1,
        var lastTriggerTime: Long = 0
    )

    override fun saveState(state: BanState) {
        try {
            if (stateFile.exists()) stateFile.copyTo(stateBackupFile, overwrite = true)
            stateFile.writeText(Json.encodeToString(state))
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 保存状态失败", e)
        }
    }

    override fun loadState(): BanState {
        return try {
            val fileToRead = when {
                stateFile.exists() -> stateFile
                stateBackupFile.exists() -> stateBackupFile
                else -> null
            } ?: return BanState()

            Json.decodeFromString<BanState>(fileToRead.readText())
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 读取状态失败", e)
            BanState()
        }
    }
}
