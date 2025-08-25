
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupFilesByFolder请求
 */
@Serializable
data class GetGroupFilesByFolderRequest(
    /**
     * 一次性获取的文件数量
     */
    @SerialName("file_count")
    val fileCount: Double? = null,

    /**
     * 和 folder_id 二选一
     */
    val folder: String? = null,

    /**
     * 和 folder 二选一
     */
    @SerialName("folder_id")
    val folderId: String? = null,

    @SerialName("group_id")
    val groupId: ID
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_files_by_folder"
}
