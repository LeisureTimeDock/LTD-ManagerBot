
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetFile请求
 */
@Serializable
data class GetFileRequest(
    /**
     * 二选一
     */
    val file: String? = null,

    /**
     * 二选一
     */
    @SerialName("file_id")
    val fileId: String? = null
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_file"
}
