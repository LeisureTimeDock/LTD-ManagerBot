
package top.r3944realms.ltdmanager.napcat.requests.passkey

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetCookies请求
 */
@Serializable
data class GetCookiesRequest(
   val domain: String
) : AbstractPassKeyRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_cookies"
}
