@file:Suppress("EXTERNAL_SERIALIZER_USELESS")

package top.r3944realms.ltdmanager.napcat.data.msghistory

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.serializer.MsgHistoryContentSerializer

@Serializable(with = MsgHistoryContentSerializer::class)
sealed class MsgHistoryContent {
    @Serializable
    class StringValue(val value: String)         : MsgHistoryContent()
    @Serializable
    class SpecificMsgList(val value: List<MsgHistorySpecificMsg>) : MsgHistoryContent()
}