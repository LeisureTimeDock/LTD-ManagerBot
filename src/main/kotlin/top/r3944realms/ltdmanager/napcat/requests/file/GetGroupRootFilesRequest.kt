
package top.r3944realms.ltdmanager.napcat.requests.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupRootFiles请求
 */
@Serializable
data class GetGroupRootFilesRequest(
    @SerialName("file_count")
    val fileCount: Double? = null,

    @SerialName("group_id")
    val groupId: ID
) : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_root_files"
}
