
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetGroupList请求
 */
@Serializable
data class GetGroupListRequest(
    @SerialName("no_cache")
   val noCache: Boolean = false
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_list"
}
