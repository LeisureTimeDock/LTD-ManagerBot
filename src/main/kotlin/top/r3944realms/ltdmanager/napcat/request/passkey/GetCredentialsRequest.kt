
package top.r3944realms.ltdmanager.napcat.request.passkey

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetCredentials请求
 */
@Serializable
data class GetCredentialsRequest(
   val domain: String
) : AbstractPassKeyRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_credentials"
}
