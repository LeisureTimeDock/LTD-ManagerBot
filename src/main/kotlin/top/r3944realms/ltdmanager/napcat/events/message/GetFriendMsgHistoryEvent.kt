
package top.r3944realms.ltdmanager.napcat.events.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.QQ
import top.r3944realms.ltdmanager.napcat.data.Sender
import top.r3944realms.ltdmanager.napcat.events.group.AbstractGroupEvent

/**
 * GetFriendMsgHistory事件
 * @property data 响应数据
 */
@Serializable
data class GetFriendMsgHistoryEvent(
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
        val messages: List<SpecificMsg>
    )
    @Serializable
    sealed class Content {
        class StringValue(val value: String)         : Content()
        class SpecificMsgList(val value: List<SpecificMsg>) : Content()
    }
    @Serializable
    data class MessageData (
        val text: String? = null,
        val name: String? = null,
        val qq: QQ? = null,
        val id: QQ? = null,
        val file: String? = null,

        /**
         * 外显
         */
        val summary: String? = null,

        val data: String? = null,
        val content: Content? = null
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
     * 文件消息
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
     * 消息forward
     */
    @Serializable
    data class Message (
        val data: MessageData,
        val type: MessageType
    )
    /**
     * 消息详情
     */
    @Serializable
    data class SpecificMsg (
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
    override fun subtype(): String {
        return "get_friend_msg_history"
    }
}
