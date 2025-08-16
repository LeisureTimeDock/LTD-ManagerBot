
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageType

/**
 * SendForwardMsg请求
 */
@Serializable
data class SendForwardMsgRequest(
    @SerialName("group_id")
    val groupId: ID? = null,

    val messages: List<TopForwardMsg>,
    val news: List<ForwardModelNews>,

    /**
     * 外显
     */
    val prompt: String,

    /**
     * 内容
     */
    val source: String,

    /**
     * 底下文本
     */
    val summary: String,

    @SerialName("user_id")
    val userId: ID? = null
) : AbstractMessageRequest() {
    /**
     * 一级合并转发消息
     */
    @Serializable
    data class TopForwardMsg (
        val data: MessageData,
        val type: MessageType
    )

    @Serializable
    data class MessageData (
        /**
         * 构建
         */
        val content: List<Message>,

        val nickname: String,

        @SerialName("user_id")
        val userId: ID
    )

    @Serializable
    sealed class ContentUnion {
        class ContentClassValue(val value: ContentClass) : ContentUnion()
        class StringValue(val value: String)             : ContentUnion()
        class MessageListValue(val value: List<Message>)     : ContentUnion()
    }

    @Serializable
    data class PurpleData (
        val text: String? = null,
        val id: ID? = null,
        val file: String? = null,

        /**
         * 外显
         *
         * 底下文本
         */
        val summary: String? = null,

        val data: String? = null,
        val name: String? = null,

        /**
         * 构建
         */
        val content: ContentUnion? = null,

        val nickname: String? = null,

        @SerialName("user_id")
        val userId: ID? = null,

        /**
         * 外显
         */
        val news: List<DataNews>? = null,

        /**
         * 外显
         */
        val prompt: String? = null,

        /**
         * 标题
         */
        val source: String? = null
    )
    /**
     * 文本消息
     *
     * 表情消息
     *
     * 图片消息
     *
     * 回复消息
     *
     * JSON消息
     *
     * 视频消息
     *
     * 文件消息
     *
     * markdown消息
     *
     * 发送forward
     *
     * 二级合并转发消息
     */
    @Serializable
    data class Message (
        val data: PurpleData,
        val type: MessageType,
    )
    @Serializable
    data class ContentClass (
        val data: FluffyData,
        val type: PurpleType
    )

    @Serializable
    data class FluffyData (
        /**
         * res_id
         */
        val id: String
    )

    @Serializable
    enum class PurpleType(val value: String) {
        @SerialName("forward") Forward("forward");
    }

    @Serializable
    data class DataNews (
        /**
         * 内容
         */
        val text: String
    )

    @Serializable
    data class ForwardModelNews (
        val text: String
    )
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_forward_msg"
}
