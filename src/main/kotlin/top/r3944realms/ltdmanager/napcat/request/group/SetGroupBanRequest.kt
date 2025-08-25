
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupBan请求
 */
@Serializable
data class SetGroupBanRequest(
    /**
     * 禁言时间：秒
     */
    val duration: Double,

    @SerialName("group_id")
    val groupId: ID,

    @SerialName("user_id")
    val userId: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_ban"
}
