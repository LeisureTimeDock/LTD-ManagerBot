
package top.r3944realms.ltdmanager.napcat.requests.message.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.requests.message.AbstractMessageRequest

/**
 * SendGroupMsg请求
 */
@Serializable
data class SendGroupMsgRequest(
    @SerialName("group_id")
    val groupId: ID,
    val message: List<MessageElement>
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_group_msg"
}
