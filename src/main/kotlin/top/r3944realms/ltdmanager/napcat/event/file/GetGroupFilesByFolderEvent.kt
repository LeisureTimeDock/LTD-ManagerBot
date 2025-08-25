
package top.r3944realms.ltdmanager.napcat.event.file

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.event.file.GetGroupRootFilesEvent.FileData

/**
 * GetGroupFilesByFolder事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupFilesByFolderEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: FileData
) : AbstractFileEvent(status0, retcode0, message0, wording0, echo0) {
    
    override fun subtype(): String {
        return "get_group_files_by_folder"
    }
}
