package top.r3944realms.ltdmanager.module.common.filter.type

import top.r3944realms.ltdmanager.module.common.CommandParser
import top.r3944realms.ltdmanager.module.common.filter.MessageFilter
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg

/** 多命令解析器匹配 */
class MultiCommandFilter(private val parsers: List<CommandParser>) : MessageFilter {
    override suspend fun test(msg: MsgHistorySpecificMsg): Boolean {
        return msg.message.any { seg ->
            seg.type == MessageType.Text && seg.data.text?.let { text ->
                parsers.any { parser -> parser.containsCommand(text) }
            } == true
        }
    }
}