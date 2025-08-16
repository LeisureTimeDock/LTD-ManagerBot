
package top.r3944realms.ltdmanager.napcat.requests.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * RenameGroupFile请求
 */
@Serializable
data class RenameGroupFileRequest(
    @SerialName("current_parent_directory")
    val currentParentDirectory: String,

    @SerialName("file_id")
    val fileId: String,

    @SerialName("group_id")
    val groupId: ID,

    @SerialName("new_name")
    val newName: String
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/rename_group_file"
}
