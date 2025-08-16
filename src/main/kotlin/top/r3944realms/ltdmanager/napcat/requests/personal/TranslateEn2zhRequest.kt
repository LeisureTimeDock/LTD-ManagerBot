
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * TranslateEn2zh请求
 */
@Serializable
data class TranslateEn2zhRequest(
    /**
     * 英文数组
     */
    val words: List<String>
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/translate_en2zh"
}
