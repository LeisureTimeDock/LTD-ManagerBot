
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupFileUrl请求
 */
@Serializable
data class GetGroupFileUrlRequest(
    @SerialName("file_id")
    val fileId: String,

    @SerialName("group_id")
    val groupId: ID
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_file_url"
}
