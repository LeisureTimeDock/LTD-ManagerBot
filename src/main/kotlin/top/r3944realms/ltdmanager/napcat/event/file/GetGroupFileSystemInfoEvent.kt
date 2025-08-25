
package top.r3944realms.ltdmanager.napcat.event.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupFileSystemInfo事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupFileSystemInfoEvent(
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
        /**
         * 文件总数
         */
        @SerialName("file_count")
        val fileCount: Double,

        /**
         * 文件上限
         */
        @SerialName("limit_count")
        val limitCount: Double,

        /**
         * 空间上限
         */
        @SerialName("total_space")
        val totalSpace: Double,

        /**
         * 已使用空间
         */
        @SerialName("used_space")
        val usedSpace: Double
    )
    override fun subtype(): String {
        return "get_group_file_system_info"
    }
}
