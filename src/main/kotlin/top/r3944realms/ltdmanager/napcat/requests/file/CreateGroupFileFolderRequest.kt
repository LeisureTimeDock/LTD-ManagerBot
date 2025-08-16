
package top.r3944realms.ltdmanager.napcat.requests.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * CreateGroupFileFolder请求
 */
@Serializable
data class CreateGroupFileFolderRequest(
    @SerialName("folder_name")
    val folderName: String,

    @SerialName("group_id")
    val groupId: ID
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/create_group_file_folder"
}
