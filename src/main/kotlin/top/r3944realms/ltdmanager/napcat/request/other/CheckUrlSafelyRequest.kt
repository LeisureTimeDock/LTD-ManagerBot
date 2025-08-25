
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * CheckUrlSafely请求
 */
@Developing
@Serializable
class CheckUrlSafelyRequest : AbstractOtherRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/check_url_safely"
}
