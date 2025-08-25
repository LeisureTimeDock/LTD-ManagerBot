
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * MarkGroupMsgAsRead请求
 */
@Serializable
data class MarkGroupMsgAsReadRequest(
    @SerialName("group_id")
    val groupId: ID
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/mark_group_msg_as_read"
}
