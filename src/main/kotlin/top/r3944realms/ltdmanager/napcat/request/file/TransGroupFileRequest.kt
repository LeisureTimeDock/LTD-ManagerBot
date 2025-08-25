
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * TransGroupFile请求
 */
@Serializable
data class TransGroupFileRequest(
    @SerialName("file_id")
    val fileId: String,

    @SerialName("group_id")
    val groupId: ID
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/trans_group_file"
}
