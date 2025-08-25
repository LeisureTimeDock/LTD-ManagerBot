
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement

/**
 * SendGroupMsg请求
 */
@Serializable
data class SendGroupMsgRequest(
    val message: List<MessageElement>,

    @SerialName("group_id")
    val groupId: ID
) : AbstractOtherRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_group_msg"
}
