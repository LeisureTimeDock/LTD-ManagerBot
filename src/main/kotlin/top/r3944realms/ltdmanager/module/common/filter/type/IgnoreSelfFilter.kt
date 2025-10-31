package top.r3944realms.ltdmanager.module.common.filter.type

import top.r3944realms.ltdmanager.module.common.filter.MessageFilter
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg

/** 忽略机器人自己的消息 */
class IgnoreSelfFilter(private val selfId: Long) : MessageFilter {
    override suspend fun test(msg: MsgHistorySpecificMsg): Boolean {
        return msg.userId != selfId
    }
}