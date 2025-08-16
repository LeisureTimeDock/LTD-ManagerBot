
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

/**
 * SendGroupSign事件
 */
@Serializable
class SendGroupSignEvent: NapCatEvent() {
    override fun type(): String {
        return "group/" + subtype()
    }

    override fun subtype(): String {
        return "send_group_sign"
    }
}
