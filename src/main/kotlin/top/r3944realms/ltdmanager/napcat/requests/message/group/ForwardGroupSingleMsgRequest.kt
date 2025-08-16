
package top.r3944realms.ltdmanager.napcat.requests.message.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.requests.message.AbstractMessageRequest

/**
 * ForwardGroupSingleMsg请求
 */
@Serializable
data class ForwardGroupSingleMsgRequest(
    @SerialName("group_id")
    val groupId: ID,

    @SerialName("message_id")
    val messageId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/forward_group_single_msg"
}
