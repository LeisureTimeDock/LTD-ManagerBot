
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetImage请求
 */
@Serializable
data class GetImageRequest(
    /**
     * 二选一
     */
    val file: String? = null,

    /**
     * 二选一
     */
    @SerialName("file_id")
    val fileId: String? = null
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_image"
}
