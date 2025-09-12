package top.r3944realms.ltdmanager.module.common.filter.type

import top.r3944realms.ltdmanager.module.common.filter.MessageFilter
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent

/** 文本关键词匹配 */
class KeywordFilter(private val keywords: Set<String>) : MessageFilter {
    override suspend fun test(msg: GetFriendMsgHistoryEvent.SpecificMsg): Boolean {
        return msg.message.any { seg ->
            seg.type == MessageType.Text && seg.data.text?.let { it in keywords } == true
        }
    }
}