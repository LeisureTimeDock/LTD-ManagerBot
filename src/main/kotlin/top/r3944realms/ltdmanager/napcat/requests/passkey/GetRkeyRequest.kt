
package top.r3944realms.ltdmanager.napcat.requests.passkey

import kotlinx.serialization.Serializable

/**
 * GetRkey请求
 */
@Serializable
class GetRkeyRequest: AbstractPassKeyRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_rkey"
}
