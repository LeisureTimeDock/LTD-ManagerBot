package top.r3944realms.ltdmanager.module.common.filter.type

import top.r3944realms.ltdmanager.module.common.cooldown.CooldownManager
import top.r3944realms.ltdmanager.module.common.filter.MessageFilter
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent

class CooldownFilter(
    private val cooldownManager: CooldownManager<*>,
    private val sendCooldown: suspend (GetFriendMsgHistoryEvent.SpecificMsg, Long) -> Unit
) : MessageFilter {

    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        val result = cooldownManager.checkAndHandle(msg.userId, msg.realId)
        if (!result.allowed && result.notify) {
            sendCooldown(msg, result.remaining)
        }
        return result.allowed
    }
}