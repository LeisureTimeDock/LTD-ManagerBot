
package top.r3944realms.ltdmanager.napcat.request.message.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.request.message.AbstractMessageRequest

/**
 * SendPrivateMsg请求
 */
@Serializable
data class SendPrivateMsgRequest(
    @SerialName("user_id")
    val userId: ID,
    val message: List<MessageElement>
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_private_msg"
}
