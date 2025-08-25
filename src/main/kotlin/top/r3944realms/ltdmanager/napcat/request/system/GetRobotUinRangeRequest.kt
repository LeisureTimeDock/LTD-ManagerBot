
package top.r3944realms.ltdmanager.napcat.request.system

import kotlinx.serialization.Serializable

/**
 * GetRobotUinRange请求
 */
@Serializable
class GetRobotUinRangeRequest : AbstractSystemRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_robot_uin_range"
}
