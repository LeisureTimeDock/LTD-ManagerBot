package top.r3944realms.ltdmanager.module.common.filter.type

import top.r3944realms.ltdmanager.module.common.filter.MessageFilter
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent
import top.r3944realms.ltdmanager.utils.Environment
import top.r3944realms.ltdmanager.utils.LoggerUtil

/** 只保留比上次触发更新的消息 */
class NewMessageFilter(
    private val getLastTrigger: (Long) -> Pair<Long, Long> // (time, realId)
) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        val (lastTime, lastRealId) = getLastTrigger(msg.userId)
        val result = msg.time > lastTime || (msg.time == lastTime && msg.realId > lastRealId)
        if (Environment.isDevelopment()) LoggerUtil.logger.debug("NewMessageFilter: msg.time=${msg.time}, msg.realId=${msg.realId}, lastTime=$lastTime, lastRealId=$lastRealId, result=$result")
        return result
    }
}