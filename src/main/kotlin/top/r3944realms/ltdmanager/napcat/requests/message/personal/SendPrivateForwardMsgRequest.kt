
package top.r3944realms.ltdmanager.napcat.requests.message.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.requests.message.AbstractMessageRequest
import top.r3944realms.ltdmanager.napcat.requests.message.SendForwardMsgRequest.ForwardModelNews
import top.r3944realms.ltdmanager.napcat.requests.message.SendForwardMsgRequest.TopForwardMsg

/**
 * SendPrivateForwardMsg请求
 */
@Serializable
data class SendPrivateForwardMsgRequest(
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
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_private_forward_msg"
}
