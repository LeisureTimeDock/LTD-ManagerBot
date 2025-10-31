package top.r3944realms.ltdmanager.napcat.data.msghistory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.data.Sender

/**
 * 消息详情
 */
@Serializable
data class MsgHistorySpecificMsg (
    val font: Long,

    @SerialName("group_id")
    val groupId: Long? = null,

    val message: List<MsgHistoryMessage>,

    @SerialName("message_format")
    val messageFormat: String,

    @SerialName("message_id")
    val messageId: Long,

    @SerialName("message_seq")
    val messageSeq: Long,

    @SerialName("message_type")
    val messageType: String,

    @SerialName("post_type")
    val postType: String,

    @SerialName("raw_message")
    val rawMessage: String,

    @SerialName("real_id")
    val realId: Long,

    @SerialName("real_seq")
    val realSeq: String,

    @SerialName("self_id")
    val selfId: Long,

    val sender: Sender,

    @SerialName("sub_type")
    val subType: String,

    val time: Long,

    @SerialName("user_id")
    val userId: Long
)