
package top.r3944realms.ltdmanager.napcat.events.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.events.group.AbstractGroupEvent

/**
 * GetImage事件
 * @property data 响应数据
 */
@Serializable
data class GetImageEvent(
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
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val base64: String,

        /**
         * 本地路径
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
         * 网络路径
         */
        val url: String
    )
    override fun subtype(): String {
        return "get_image"
    }
}
