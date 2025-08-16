
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetGroupSystemMsg请求
 */
@Serializable
data class GetGroupSystemMsgRequest(
   val count: Int = 50
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_system_msg"
}
