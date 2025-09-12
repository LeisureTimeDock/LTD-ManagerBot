package top.r3944realms.ltdmanager.module.common

import top.r3944realms.ltdmanager.module.common.cooldown.CooldownManager
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent

class TriggerMessageFilter(private val filters: List<MessageFilter>) {
    suspend fun filter(messages: List<GetFriendMsgHistoryEvent.SpecificMsg>)
            : List<GetFriendMsgHistoryEvent.SpecificMsg> {

        val result = mutableListOf<GetFriendMsgHistoryEvent.SpecificMsg>()
        for (msg in messages) {
            if (filters.all { it.test(msg) }) {
                result.add(msg)
            }
        }
        return result
    }
}
interface MessageFilter {
    suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean
}
/** 忽略机器人自己的消息 */
class IgnoreSelfFilter(private val selfId: Long) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        return msg.userId != selfId
    }
}

/** 只保留比上次触发更新的消息 */
class NewMessageFilter(
    private val getLastTrigger: (Long) -> Pair<Long, Long> // (time, realId)
) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        val (lastTime, lastRealId) = getLastTrigger(msg.userId)
        return msg.time > lastTime || (msg.time == lastTime && msg.realId > lastRealId)
    }
}

/** 文本关键词匹配 */
class KeywordFilter(private val keywords: Set<String>) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        return msg.message.any { seg ->
            seg.type == MessageType.Text && seg.data.text?.let { it in keywords } == true
        }
    }
}

/** 命令解析器匹配 */
class CommandFilter(private val parser: CommandParser) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        return msg.message.any { seg ->
            seg.type == MessageType.Text && seg.data.text?.let { parser.containsCommand(it) } == true
        }
    }
}

/** 冷却检查 */
class CooldownFilter(
    private val cooldownManager: CooldownManager<*>,
    private val sendCooldown: suspend (GetFriendMsgHistoryEvent.SpecificMsg, Long) -> Unit
) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        val result = cooldownManager.check(msg.userId, msg.realId, msg.time)
        if (!result.canTrigger) {
            sendCooldown(msg, result.remainingSeconds)
        }
        return result.canTrigger
    }
}