
package top.r3944realms.ltdmanager.napcat.requests.file

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
    val fileID: String
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_private_file_url"
}
