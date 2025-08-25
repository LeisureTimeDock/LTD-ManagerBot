
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement

/**
 * SendPrivateMsg请求
 */
@Serializable
data class SendPrivateMsgRequest(
    val message: List<MessageElement>,

    @SerialName("user_id")
    val userId: ID
) : AbstractOtherRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_private_msg"
}
