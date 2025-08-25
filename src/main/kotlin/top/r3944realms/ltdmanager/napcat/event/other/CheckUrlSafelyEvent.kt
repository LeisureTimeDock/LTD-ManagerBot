
package top.r3944realms.ltdmanager.napcat.event.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

/**
 * CheckUrlSafely事件
 */
@Developing
@Serializable
class CheckUrlSafelyEvent: NapCatEvent() {
    override fun type(): String = "other/" + subtype()

    override fun subtype(): String {
        return "check_url_safely"
    }
}
