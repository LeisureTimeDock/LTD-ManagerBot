
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupMemberList请求
 */
@Serializable
data class GetGroupMemberListRequest(
    @SerialName("group_id")
    val groupId: ID,

    @SerialName("no_cache")
    val noCache: Boolean,

) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_member_list"
}
