
package top.r3944realms.ltdmanager.napcat.requests.other

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement

/**
 * SendMsg请求
 */
@Serializable
data class SendMsgRequest(
    val message: List<MessageElement>,
    @SerialName("message_type")
    val messageType: MessageType,
    @SerialName("group_id")
    val groupId: ID,
    @SerialName("user_id")
    val userId: ID
) : AbstractOtherRequest() {
    @Serializable
    enum class MessageType(val value: String) {
        @SerialName("private")PRIVATE("private"),
        @SerialName("group")GROUP("group"),
    }
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_msg"
}
