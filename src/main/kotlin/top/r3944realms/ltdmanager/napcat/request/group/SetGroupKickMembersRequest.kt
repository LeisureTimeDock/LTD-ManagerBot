
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupKickMembers请求
 */
@Serializable
data class SetGroupKickMembersRequest(
    @SerialName("group_id")
    val groupId: ID,

    /**
     * 是否群拉黑
     */
    @SerialName("reject_add_request")
    val rejectAddRequest: Boolean? = null,

    @SerialName("user_id")
    val userId: List<ID>
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_kick_members"
}
