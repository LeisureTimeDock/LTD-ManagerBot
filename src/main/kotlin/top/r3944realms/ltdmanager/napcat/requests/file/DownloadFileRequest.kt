
package top.r3944realms.ltdmanager.napcat.requests.file

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * DownloadFile请求
 */
@Serializable
data class DownloadFileRequest(
    /**
     * 和url二选一
     */
    val base64: String? = null,

    /**
     * 请求头
     */
    val headers: Headers? = null,

    /**
     * 自定义文件名称
     */
    val name: String? = null,

    /**
     * 下载地址
     */
    val url: String? = null
) : AbstractFileRequest() {
    /**
     * 请求头
     */
    @Serializable
    sealed class Headers {
        class StringArrayValue(val value: List<String>) : Headers()
        class StringValue(val value: String)            : Headers()
    }
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/download_file"
}
