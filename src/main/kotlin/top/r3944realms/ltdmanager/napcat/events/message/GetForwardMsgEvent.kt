
package top.r3944realms.ltdmanager.napcat.events.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.Qq
import top.r3944realms.ltdmanager.napcat.data.Sender
import top.r3944realms.ltdmanager.napcat.events.group.AbstractGroupEvent

/**
 * GetForwardMsg事件
 * @property data 响应数据
 */
@Serializable
data class GetForwardMsgEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: Data
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val messages: List<Msg>
    )
    /**
     * 获取合并转发消息
     */
    @Serializable
    data class Msg (
        val font: Double,

        @SerialName("group_id")
        val groupID: Double? = null,

        val message: List<Message>,

        @SerialName("message_format")
        val messageFormat: String,

        @SerialName("message_id")
        val messageID: Double,

        @SerialName("message_seq")
        val messageSeq: Double,

        @SerialName("message_type")
        val messageType: String,

        @SerialName("post_type")
        val postType: String,

        @SerialName("raw_message")
        val rawMessage: String,

        @SerialName("real_id")
        val realID: Double,

        @SerialName("real_seq")
        val realSeq: String,

        @SerialName("self_id")
        val selfID: Double,

        val sender: Sender,

        @SerialName("sub_type")
        val subType: String,

        val time: Double,

        @SerialName("user_id")
        val userID: Double
    )
    /**
     * 文本消息
     *
     * 艾特消息
     *
     * 表情消息
     *
     * 图片消息
     *
     * 回复消息
     *
     * JSON消息
     *
     * 语音消息
     *
     * 视频消息
     *
     * markdown消息
     *
     * 获取合并转发消息
     */
    @Serializable
    data class Message (
        val data: MessageData? = null,
        val type: MessageType? = null,
        val font: Double? = null,

        @SerialName("group_id")
        val groupID: Double? = null,

        val message: List<Message>? = null,

        @SerialName("message_format")
        val messageFormat: String? = null,

        @SerialName("message_id")
        val messageID: Double? = null,

        @SerialName("message_seq")
        val messageSeq: Double? = null,

        @SerialName("message_type")
        val messageType: String? = null,

        @SerialName("post_type")
        val postType: String? = null,

        @SerialName("raw_message")
        val rawMessage: String? = null,

        @SerialName("real_id")
        val realID: Double? = null,

        @SerialName("real_seq")
        val realSeq: String? = null,

        @SerialName("self_id")
        val selfID: Double? = null,

        val sender: Sender? = null,

        @SerialName("sub_type")
        val subType: String? = null,

        val time: Double? = null,

        @SerialName("user_id")
        val userID: Double? = null
    )
    @Serializable
    data class MessageData (
        val text: String? = null,
        val name: String? = null,
        val qq: Qq? = null,
        val id: Qq? = null,
        val file: String? = null,

        /**
         * 外显
         */
        val summary: String? = null,

        val data: String? = null,
        val content: String? = null
    )
    override fun subtype(): String {
        return "get_forward_msg"
    }
}
