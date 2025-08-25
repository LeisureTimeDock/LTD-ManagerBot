
package top.r3944realms.ltdmanager.napcat.event.file

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupRootFiles事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupRootFilesEvent(
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
    @Serializable
    data class FileData (
        /**
         * 文件列表
         */
        val files: List<GroupFileInformation>,

        /**
         * 文件夹列表
         */
        val folders: List<GroupFolderInformation>
    )


    override fun subtype(): String {
        return "get_group_root_files"
    }
}
