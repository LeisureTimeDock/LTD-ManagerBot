
package top.r3944realms.ltdmanager.napcat.events.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetFile事件
 * @property data 响应数据
 */
@Serializable
data class GetFileEvent(
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
        val base64: String,

        /**
         * 路径或链接
         */
        val file: String,

        /**
         * 文件名
         */
        @SerialName("file_name")
        val fileName: String,

        /**
         * 文件大小
         */
        @SerialName("file_size")
        val fileSize: String,

        /**
         * 路径或链接
         */
        val url: String
    )
    override fun subtype(): String {
        return "get_file"
    }
}
