
package top.r3944realms.ltdmanager.napcat.request.passkey

import kotlinx.serialization.Serializable

/**
 * GetRkeyServer请求
 */
@Serializable
class GetRkeyServerRequest: AbstractPassKeyRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_rkey_server"
}
