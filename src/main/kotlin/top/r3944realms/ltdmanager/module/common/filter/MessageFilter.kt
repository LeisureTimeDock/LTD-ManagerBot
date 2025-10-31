package top.r3944realms.ltdmanager.module.common.filter

import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg

interface MessageFilter {
    suspend fun test(msg: MsgHistorySpecificMsg): Boolean
}