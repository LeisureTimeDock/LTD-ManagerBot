
package top.r3944realms.ltdmanager.napcat.request.message.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.request.message.AbstractMessageRequest

/**
 * ForwardFriendSingleMsg请求
 */
@Serializable
data class ForwardFriendSingleMsgRequest(
    @SerialName("message_id")
    val messageId: ID,

    @SerialName("user_id")
    val userId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/forward_friend_single_msg"
}
