package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.module.common.CommandParser
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.MultiCommandFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupShutListEvent
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupShutListRequest
import top.r3944realms.ltdmanager.napcat.request.group.SetGroupBanRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import kotlin.random.Random

/**
 * 指令触发禁言模块
 */
class BanModule(
    moduleName: String,
    private val groupMessagePollingModule : GroupMessagePollingModule,
    private val selfId: Long,
    private val adminsId: List<Long> = listOf(),
    muteCommandPrefixList: List<String> = listOf("mute"), // 默认命令前缀
    unmuteCommandPrefixList: List<String> = listOf("unmute"),
    private val minBanMinutes: Int = 1,
    private val maxBanMinutes: Int = 15,
    private val factorX: Int = 2,         // 系数 x，禁言倍数

) : BaseModule("BanModule", moduleName), PersistentState<BanModule.BanState> {

    private val banCommandParse = CommandParser(muteCommandPrefixList)
    private val pardonCommandParse = CommandParser(unmuteCommandPrefixList)
    private val multiCommandFilter = MultiCommandFilter(listOf(banCommandParse, pardonCommandParse))
    private val stateFile: File = getStateFileInternal("command_ban_state.json", name)
    private val stateBackupFile: File = getStateFileInternal("command_ban_state.json.bak", name)
    private var banState = loadState()
    override fun getState(): BanState = banState

    private val triggerFilter by lazy {
        TriggerMessageFilter(
            listOf(
                IgnoreSelfFilter(selfId),
                NewMessageFilter { userId ->
                    banState.getLastTriggerTime(userId) to banState.getLastTriggerRealId(userId)
                },
                multiCommandFilter
            )
        )
    }

    private var scope: CoroutineScope? = null


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

    private suspend fun handleMessages(messages: List<MsgHistorySpecificMsg>) {
        // 先过一遍过滤器，只有符合条件的才进入后续处理
        val filtered = triggerFilter.filter(messages)
        for (msg in filtered) {
            processBanCommand(msg)
            processUnBanCommand(msg)
        }
    }
    /**
     * 将 SpecificMsg 中的 message 段拼成一条“可解析文本”。
     * - text 段直接拼接
     * - 如果消息段里包含 @（在 MessageData 中为 qq 字段），则拼成 "@{qq}"，方便 parseMentionToUserId 解析
     */
    private fun MsgHistorySpecificMsg.plainText(): String {
        return this.message.joinToString(" ") { seg ->
            // 如果 message element 包含 qq 字段（即@用户），优先使用它
            seg.data.qq?.let { "@${it}" } ?: (seg.data.text ?: "")
        }.trim()
    }
    /**
     * 从消息段中提取所有被 @ 的用户 ID
     */
    private fun MsgHistorySpecificMsg.getMentionedUserIds(): List<ID> {
        return this.message
            .filter { it.type == MessageType.At && it.data.qq != null }
            .mapNotNull { it.data.qq }
            .distinctBy {
                when (it) {
                    is ID.StringValue -> it.value
                    is ID.LongValue -> it.value
                }
            }
    }
    private suspend fun processUnBanCommand(msg: MsgHistorySpecificMsg) {
        try {
            pardonCommandParse.parseCommand(msg.plainText()) ?: return
            // 获取所有被 @ 的用户
            val mentionedUserIds = msg.getMentionedUserIds().map {
                when (it) {
                    is ID.StringValue -> it.value.toLong()
                    is ID.LongValue -> it.value
                }
            } // List<Long>
            val send =
                napCatClient.send<GetGroupShutListEvent>(GetGroupShutListRequest(ID.long(groupMessagePollingModule.targetGroupId)))
            val muteList = send.data.map { it.uin.toLong() }
            for (target in mentionedUserIds) {
                if(target !in muteList) {
                    sendGroupMessage("❌ 目标用户未被禁言",
                        msg.realId
                    )
                } else {
                    banUser(ID.long(target), groupMessagePollingModule.targetGroupId, 0)
                    sendGroupMessage(
                        "✅ 已解禁对方@(${target})",
                        msg.realId
                    )
                }

            }

            // 更新状态
            banState = banState.updateLastTrigger(msg.userId, msg.realId, msg.time)
            saveState(banState)
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 执行解禁言指令失败", e)
            sendGroupMessage("❌ 执行解禁言失败，请检查解指令格式或权限", msg.realId)
            banState = banState.updateLastTrigger(msg.sender.userId, msg.realId, msg.time)
            saveState(banState)
        }
    }
    private suspend fun processBanCommand(msg: MsgHistorySpecificMsg) {
        try {
            val parsed = banCommandParse.parseCommand(msg.plainText()) ?: return
            val (_, argument) = parsed

            val parts = argument.split(" ").filter { it.isNotBlank() }

            // 解析禁言时间
            val durationMinutes = parts.getOrNull(0)?.toIntOrNull()
                ?: Random.nextInt(minBanMinutes, maxBanMinutes + 1)
            val durationSeconds = durationMinutes.coerceIn(minBanMinutes, maxBanMinutes) * 60

            // 获取所有被 @ 的用户
            val mentionedUserIds = msg.getMentionedUserIds() // List<ID>
            val targets = mentionedUserIds.ifEmpty { listOf(ID.long(msg.sender.userId)) }

            for (target in targets) {
                val targetLongId = when (target) {
                    is ID.StringValue -> target.value.toLong()
                    is ID.LongValue -> target.value
                }

                // 权限检查：非管理员不能禁言多个他人
                if (mentionedUserIds.isNotEmpty() && mentionedUserIds.size != 1 && msg.sender.userId !in adminsId) {
                    sendGroupMessage("❌ 你没有权限使用禁言多用户功能", msg.realId)
                    continue
                }

                // 禁言机器人跳过
                if (targetLongId == selfId) {
                    sendGroupMessage("❌ 你没有权限禁言机器人", msg.realId)
                    continue
                }
                if (targetLongId in adminsId) {
                    sendGroupMessage("❌ 不支持禁言管理员", msg.realId)
                    continue
                }

                // 单 @ 且非自己，可能触发反禁自己
                if (mentionedUserIds.size == 1 && targetLongId != msg.sender.userId && msg.sender.userId !in adminsId) {
                    val dice = Random.nextInt(1, 7) // 1~6
                    val chance = when (dice) {
                        6 -> 100
                        5 -> 80
                        4 -> 60
                        3 -> 50
                        2 -> 20
                        1 -> 0
                        else -> 0
                    }

                    val selfDuration = durationSeconds * factorX
                    if (Random.nextInt(0,100) > chance) {
                        // 触发反禁自己
                        banUser(ID.long(msg.sender.userId), groupMessagePollingModule.targetGroupId, selfDuration)
                        sendGroupMessage(
                            "⚠️ 骰子点数: $dice, 成功概率: ${chance}% → 失败，你触发了反禁，禁言 ${selfDuration / 60} 分钟",
                            msg.realId
                        )
                    } else {
                        // 未触发反禁自己，禁言目标
                        banUser(target, groupMessagePollingModule.targetGroupId, durationSeconds)
                        sendGroupMessage(
                            "✅ 骰子点数: $dice, 成功概率: ${chance}% → 成功禁言 <@${targetLongId}>",
                            msg.realId
                        )
                    }
                } else {
                    // 多 @ 或管理员操作，直接禁言目标
                    banUser(target, groupMessagePollingModule.targetGroupId, durationSeconds)
                    sendGroupMessage(
                        if (targetLongId == msg.sender.userId) {
                            "✅ 你已被禁言 ${durationSeconds/ 60} 分钟"
                        } else {
                            "✅ 已禁言 <@${targetLongId}> ${durationSeconds/ 60} 分钟"
                        },
                        msg.realId
                    )
                }


            }
            // 更新状态
            banState = banState.updateLastTrigger(msg.userId, msg.realId, msg.time)
            saveState(banState)
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 执行禁言指令失败", e)
            sendGroupMessage("❌ 执行禁言失败，请检查指令格式或权限", msg.realId)
            banState = banState.updateLastTrigger(msg.sender.userId, msg.realId, msg.time)
            saveState(banState)
        }
    }

    private suspend fun banUser(userId: ID, groupId: Long, seconds: Int) {
        val request = SetGroupBanRequest(
            duration = seconds.toDouble(),
            groupId = ID.long(groupId),
            userId = userId
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
        return buildString {
            append("[$name] 指令禁言模块：\n")
            append(" 管理员用户ID: ${adminsId}\n")
            append(" - 用户发送 ${banCommandParse.getCommands().joinToString("、")} 来禁言自己或指定其他用户（需管理员权限）。\n")
            append(" - 支持指定禁言分钟数或随机分钟数，范围 $minBanMinutes-$maxBanMinutes 分钟。\n")
            append(" - 支持对单个 @ 用户禁言，有概率反禁自己（骰子点数决定概率）。\n")
            append(" - 管理员可以禁言其他用户；非管理员尝试多个禁言对象会收到无权限提示。\n")
            append(" - 用户发送 ${pardonCommandParse.getCommands().joinToString("、")} 来解禁指定用户。\n")
            append(" - 仅支持对单个 @ 用户解禁言。\n")
        }
    }

    override fun help(): String {
        return buildString {
            appendLine("📖 [$name] 使用帮助：")
            appendLine("指令格式：${banCommandParse.getCommands().joinToString("、")} [分钟] [@用户...]")
            appendLine("示例：")
            appendLine(" - <指令>             → 随机禁言自己")
            appendLine(" - <指令> 5           → 禁言自己 5 分钟")
            appendLine(" - <指令> 4 @User123  → 禁言指定用户 4 分钟（可能失败）")
            appendLine(" - <指令> 4 @User123 @User22  → 禁言指定多用户 4 分钟（需在程序管理员列表中）")
            appendLine()
            appendLine("⚠️ 特殊说明：")
            appendLine(" - 如果 @ 单个用户且执行者非需在程序管理员，有 y% 概率触发反禁自己，")
            appendLine("   骰子点数决定概率：6 → 100%, 5 → 80%, 4 → 60%, 3 → 50%, 2 → 20%, 1 → 0%")
            appendLine(" - 禁言机器人自身不会生效")
            appendLine(" - 禁言状态会自动保存以便下次使用")
            appendLine()
            appendLine("指令格式：${pardonCommandParse.getCommands().joinToString("、")} [@用户]")
            appendLine("示例：")
            appendLine(" - <指令> @User123  → 解禁指定用户")
        }
    }

    // ---------------- 持久化 ----------------
    @Serializable
    data class UserBanDetail(
        val realId: Long,
        val time: Long,
    )

    @Serializable
    data class BanState(
        val map: Map<Long, UserBanDetail> = emptyMap()
    ) {
        fun getLastTriggerTime(userId: Long): Long = map[userId]?.time ?: -1
        fun getLastTriggerRealId(userId: Long): Long = map[userId]?.realId ?: -1

        fun updateLastTrigger(userId: Long, realId: Long, time: Long = -1): BanState {
            val old = map[userId]
            val newTime = if (time != -1L) time else old?.time ?: -1
            val newMap = map.toMutableMap().apply {
                put(userId, UserBanDetail(realId, newTime))
            }
            return copy(map = newMap)
        }
    }

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
