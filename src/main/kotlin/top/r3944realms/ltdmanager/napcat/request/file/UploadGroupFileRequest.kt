
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * UploadGroupFile请求
 */
@Serializable
data class UploadGroupFileRequest(
    val file: String,

    /**
     * 文件夹ID（二选一）
     */
    val folder: String? = null,

    /**
     * 文件夹ID（二选一）
     */
    @SerialName("folder_id")
    val folderId: String? = null,

    @SerialName("group_id")
    val groupId: ID,

    val name: String
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/upload_group_file"
}
