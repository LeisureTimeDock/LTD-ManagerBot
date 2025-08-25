
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupSearch请求
 */
@Serializable
data class SetGroupSearchRequest(
    @SerialName("group_id")
    val groupId: ID,

    @SerialName("no_code_finger_open")
    val noCodeFingerOpen: Double? = null,

    @SerialName("no_finger_open")
    val noFingerOpen: Double? = null
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_search"
}
