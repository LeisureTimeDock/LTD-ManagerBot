
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.Serializable

/**
 * CanSendImage请求
 */
@Serializable
class CanSendImageRequest : AbstractPersonalRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/can_send_image"
}
