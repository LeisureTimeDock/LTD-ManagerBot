
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * OcrImage请求
 */
@Serializable
data class OcrImageRequest(
   val image: String
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/ocr_image"
}
