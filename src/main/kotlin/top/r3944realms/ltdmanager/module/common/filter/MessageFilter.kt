package top.r3944realms.ltdmanager.module.common.filter

import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent

interface MessageFilter {
    suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean
}