
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupLeave请求
 */
@Serializable
data class SetGroupLeaveRequest(
    @SerialName("group_id")
    val groupId: ID,

    /**
     * 暂无作用
     */
    @SerialName("is_dismiss")
    val isDismiss: Boolean? = null
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_leave"
}
