
package top.r3944realms.ltdmanager.napcat.requests.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * MoveGroupFile请求
 */
@Serializable
data class MoveGroupFileRequest(
    /**
     * 当前父目录，根目录填  /
     */
    @SerialName("current_parent_directory")
    val currentParentDirectory: String,

    @SerialName("file_id")
    val fileId: String,

    @SerialName("group_id")
    val groupId: ID,

    /**
     * 目标父目录
     */
    @SerialName("target_parent_directory")
    val targetParentDirectory: String
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/move_group_file"
}
