
package top.r3944realms.ltdmanager.napcat.request.message.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.request.message.AbstractMessageRequest
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest.ForwardModelNews
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest.TopForwardMsg

/**
 * SendGroupForwardMsg请求
 */
@Serializable
data class SendGroupForwardMsgRequest(
    @SerialName("group_id")
    val groupId: ID,
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
    val summary: String
) : AbstractMessageRequest() {

    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_group_forward_msg"
}
