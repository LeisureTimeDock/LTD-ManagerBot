
package top.r3944realms.ltdmanager.napcat.events.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import top.r3944realms.ltdmanager.napcat.Developing
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

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
