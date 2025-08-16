
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupAdmin请求
 */
@Serializable
data class SetGroupAdminRequest(
    val enable: Boolean,

    @SerialName("group_id")
    val groupID: ID,

    @SerialName("user_id")
    val userID: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_admin"
}
