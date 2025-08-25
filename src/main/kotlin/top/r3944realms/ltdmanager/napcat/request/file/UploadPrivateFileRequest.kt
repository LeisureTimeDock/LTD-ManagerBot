
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * UploadPrivateFile请求
 */
@Serializable
data class UploadPrivateFileRequest(
    val file: String,
    val name: String,

    @SerialName("user_id")
    val userId: ID
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/upload_private_file"
}
