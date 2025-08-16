
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupSpecialTitle请求
 */
@Serializable
data class SetGroupSpecialTitleRequest(
    @SerialName("group_id")
    val groupID: ID,

    /**
     * 为空则取消头衔
     */
    @SerialName("special_title")
    val specialTitle: String? = null,

    @SerialName("user_id")
    val userID: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_special_title"
}
