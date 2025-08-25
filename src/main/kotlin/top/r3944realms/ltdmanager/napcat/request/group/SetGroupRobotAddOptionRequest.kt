
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupRobotAddOption请求
 */
@Serializable
data class SetGroupRobotAddOptionRequest(
    @SerialName("group_id")
    val groupId: ID,

    @SerialName("robot_member_examine")
    val robotMemberExamine: Double? = null,

    @SerialName("robot_member_switch")
    val robotMemberSwitch: Double? = null
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_robot_add_option"
}
