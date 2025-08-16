
package top.r3944realms.ltdmanager.napcat.events.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * CreateGroupFileFolder事件
 * @property data 响应数据
 */
@Serializable
data class CreateGroupFileFolderEvent(
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

    val data: Data
) : AbstractFileEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val groupItem: GroupItem,
        val result: Result
    )

    @Serializable
    data class GroupItem (
        val fileInfo: String? = null,

        /**
         * 文件夹信息
         */
        val folderInfo: FolderInfo,

        @SerialName("peerId")
        val peerID: String,

        val type: Double
    )

    /**
     * 文件夹信息
     */
    @Serializable
    data class FolderInfo (
        val createTime: Double,
        val createUin: String,
        val creatorName: String,

        @SerialName("folderId")
        val folderID: String,

        val folderName: String,
        val modifyName: String,
        val modifyTime: Double,
        val modifyUin: String,

        @SerialName("parentFolderId")
        val parentFolderID: String,

        val totalFileCount: Double,
        val usedSpace: String
    )

    @Serializable
    data class Result (
        val clientWording: String,
        val retCode: Double,
        val retMsg: String
    )
    override fun subtype(): String {
        return "create_group_file_folder"
    }
}
