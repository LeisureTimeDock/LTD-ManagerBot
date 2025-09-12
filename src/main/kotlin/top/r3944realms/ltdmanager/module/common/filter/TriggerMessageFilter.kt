package top.r3944realms.ltdmanager.module.common.filter

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