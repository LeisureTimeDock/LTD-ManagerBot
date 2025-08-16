
package top.r3944realms.ltdmanager.napcat.requests.passkey

import kotlinx.serialization.Serializable

/**
 * NcGetRkey请求
 */
@Serializable
class NcGetRkeyRequest : AbstractPassKeyRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/nc_get_rkey"
}
