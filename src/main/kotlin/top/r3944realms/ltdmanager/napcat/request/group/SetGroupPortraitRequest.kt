
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupPortrait请求
 */
@Serializable
data class SetGroupPortraitRequest(
    val file: String,

    @SerialName("group_id")
    val groupId: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_portrait"
}
