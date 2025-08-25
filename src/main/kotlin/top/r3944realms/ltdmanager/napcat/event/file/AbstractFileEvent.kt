package top.r3944realms.ltdmanager.napcat.event.file

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

/**
 * QQ 文件相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
 */
@Serializable
abstract class AbstractFileEvent(
    /**
     * 状态字符串
     */
    open val status: Status,
    /**
     * 返回代码
     */
    open val retcode: Double,
    /**
     * 消息
     */
    open val message: String,
    /**
     * 文字描述
     */
    open val wording: String,
    /**
     * 回显字段
     */
    open val echo: String? = null
) : NapCatEvent() {
    @Serializable
    data class OkData(
        val ok:Boolean
    )
    @Serializable
    data class UrlData(
        val url: String
    )
    /**
     * 群文件信息
     */
    @Serializable
    data class GroupFileInformation (
        val busid: Double,

        @SerialName("dead_time")
        val deadTime: Double,

        @SerialName("download_times")
        val downloadTimes: Double,

        @SerialName("file_id")
        val fileId: String,

        @SerialName("file_name")
        val fileName: String,

        @SerialName("file_size")
        val fileSize: Double,

        @SerialName("group_id")
        val groupId: Double,

        @SerialName("modify_time")
        val modifyTime: Double,

        val size: Double,

        @SerialName("upload_time")
        val uploadTime: Double,

        val uploader: Double,

        @SerialName("uploader_name")
        val uploaderName: String
    )

    /**
     * 群文件夹信息
     */
    @Serializable
    data class GroupFolderInformation (
        /**
         * 创建时间
         */
        @SerialName("create_time")
        val createTime: Double,

        /**
         * 创建人账号
         */
        val creator: Double,

        /**
         * 创建人昵称
         */
        @SerialName("creator_name")
        val creatorName: String,

        val folder: String,

        @SerialName("folder_id")
        val folderID: String,

        /**
         * 文件夹名称
         */
        @SerialName("folder_name")
        val folderName: String,

        @SerialName("group_id")
        val groupId: Double,

        /**
         * 文件数量
         */
        @SerialName("total_file_count")
        val totalFileCount: Double
    )
    override fun type(): String = "file/" + subtype()
    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("file/move_group_file", MoveGroupFileEvent.serializer())
                put("file/trans_group_file", TransGroupFileEvent.serializer())
                put("file/rename_group_file", RenameGroupFileEvent.serializer())
                put("file/upload_group_file",UploadGroupFileEvent.serializer())
                put("file/create_group_file_folder",CreateGroupFileFolderEvent.serializer())
                put("file/delete_group_file", DeleteGroupFileEvent.serializer())
                put("file/delete_group_folder", DeleteGroupFolderEvent.serializer())
                put("file/get_group_file_system_info", GetGroupFileSystemInfoEvent.serializer())
                put("file/get_group_root_files", GetGroupRootFilesEvent.serializer())
                put("file/get_group_files_by_folder", GetGroupFilesByFolderEvent.serializer())
                put("file/get_group_file_url",GetGroupFileUrlEvent.serializer())
                put("file/upload_private_file", UploadGroupFileEvent.serializer())
                put("file/get_private_file_url", GetPrivateFileUrlEvent.serializer())
                put("file/get_file", GetFileEvent.serializer())
                put("file/download_file", DownloadFileEvent.serializer())
                put("file/clean_cache", CleanCacheEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(MoveGroupFileEvent::class)
                        subclass(TransGroupFileEvent::class)
                        subclass(RenameGroupFileEvent::class)
                        subclass(UploadGroupFileEvent::class)
                        subclass(CreateGroupFileFolderEvent::class)
                        subclass(DeleteGroupFileEvent::class)
                        subclass(DeleteGroupFolderEvent::class)
                        subclass(GetGroupFileSystemInfoEvent::class)
                        subclass(GetGroupRootFilesEvent::class)
                        subclass(GetGroupFilesByFolderEvent::class)
                        subclass(GetGroupFileUrlEvent::class)
                        subclass(UploadGroupFileEvent::class)
                        subclass(GetPrivateFileUrlEvent::class)
                        subclass(GetFileEvent::class)
                        subclass(DownloadFileEvent::class)
                        subclass(CleanCacheEvent::class)
                    }
                }
            }
        }
    }
}