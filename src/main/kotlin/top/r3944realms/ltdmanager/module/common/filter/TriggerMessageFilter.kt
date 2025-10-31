package top.r3944realms.ltdmanager.module.common.filter

import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg

class TriggerMessageFilter(private val filters: List<MessageFilter>) {
    suspend fun filter(messages: List<MsgHistorySpecificMsg>)
            : List<MsgHistorySpecificMsg> {

        val result = mutableListOf<MsgHistorySpecificMsg>()
        for (msg in messages) {
            if (filters.all { it.test(msg) }) {
                result.add(msg)
            }
        }
        return result
    }
}