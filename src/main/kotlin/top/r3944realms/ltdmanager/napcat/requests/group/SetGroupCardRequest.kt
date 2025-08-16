
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupCard请求
 */
@Serializable
data class SetGroupCardRequest(
    /**
     * 为空则为取消群名片
     */
    val card: String? = null,

    @SerialName("group_id")
    val groupID: ID,

    @SerialName("user_id")
    val userID: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_card"
}
