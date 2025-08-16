
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SetGroupRemark请求
 */
@Serializable
data class SetGroupRemarkRequest(
    @SerialName("group_id")
    val groupId: String,

    val remark: String
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_remark"
}
