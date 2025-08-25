
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * DeleteEssenceMsg请求
 */
@Serializable
data class DeleteEssenceMsgRequest(
    @SerialName("message_id")
    val messageId: ID
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/delete_essence_msg"
}
