
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetEssenceMsgList事件
 * @property data 响应数据
 */
@Serializable
data class GetEssenceMsgListEvent(
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

    val data: List<Datum>
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        /**
         * 消息内容
         */
        val content: List<Content>,

        @SerialName("message_id")
        val messageID: Double,

        @SerialName("msg_random")
        val msgRandom: Double,

        @SerialName("msg_seq")
        val msgSeq: Double,

        /**
         * 设精人账号
         */
        @SerialName("operator_id")
        val operatorID: Double,

        /**
         * 设精人昵称
         */
        @SerialName("operator_nick")
        val operatorNick: String,

        /**
         * 设精时间
         */
        @SerialName("operator_time")
        val operatorTime: Double,

        /**
         * 发送人账号
         */
        @SerialName("sender_id")
        val senderID: Double,

        /**
         * 发送人昵称
         */
        @SerialName("sender_nick")
        val senderNick: String
    )
    /**
     * 文本消息
     */
    @Serializable
    data class Content (
        val data: Data,
        val type: Type
    )

    @Serializable
    data class Data (
        val text: String? = null,
        val url: String? = null
    )

    @Serializable
    enum class Type(val value: String) {
        @SerialName("image") Image("image"),
        @SerialName("text") Text("text");
    }
    override fun subtype(): String {
        return "get_essence_msg_list"
    }
}
