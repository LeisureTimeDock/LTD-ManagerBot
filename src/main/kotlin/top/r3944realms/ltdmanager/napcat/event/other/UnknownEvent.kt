
package top.r3944realms.ltdmanager.napcat.event.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

/**
 * Unknown事件
 */
@Developing
@Serializable
class UnknownEvent: NapCatEvent() {
    override fun type(): String {
        return "other/" + subtype()
    }

    override fun subtype(): String {
        return "unknown"
    }
}
