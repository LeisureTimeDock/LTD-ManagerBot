
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * MarkPrivateMsgAsRead请求
 */
@Serializable
data class MarkPrivateMsgAsReadRequest(
    @SerialName("user_id")
    val userId: ID
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/mark_private_msg_as_read"
}
