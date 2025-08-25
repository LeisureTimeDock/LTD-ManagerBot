
package top.r3944realms.ltdmanager.napcat.request.passkey

import kotlinx.serialization.Serializable

/**
 * GetClientkey请求
 */
@Serializable
class GetClientkeyRequest: AbstractPassKeyRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_clientkey"
}
