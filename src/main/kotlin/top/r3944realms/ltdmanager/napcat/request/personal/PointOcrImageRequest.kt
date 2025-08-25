
package top.r3944realms.ltdmanager.napcat.request.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * PointOcrImage请求
 */
@Serializable
data class PointOcrImageRequest(
    val image: String
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/.ocr_image"
}
