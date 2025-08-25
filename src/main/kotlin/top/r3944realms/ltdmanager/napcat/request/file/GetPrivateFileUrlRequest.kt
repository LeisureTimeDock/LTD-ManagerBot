
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetPrivateFileUrl请求
 */
@Serializable
data class GetPrivateFileUrlRequest(
    @SerialName("file_id")
    val fileId: String
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_private_file_url"
}
