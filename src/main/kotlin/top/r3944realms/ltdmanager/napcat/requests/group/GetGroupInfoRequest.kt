
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupInfo请求
 */
@Serializable
data class GetGroupInfoRequest(
    @SerialName("group_id")
    val groupId: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_info"
}
