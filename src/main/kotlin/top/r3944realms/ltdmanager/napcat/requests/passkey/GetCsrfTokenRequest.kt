
package top.r3944realms.ltdmanager.napcat.requests.passkey

import kotlinx.serialization.Serializable

/**
 * GetCsrfToken请求
 */
@Serializable
class GetCsrfTokenRequest : AbstractPassKeyRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_csrf_token"
}
