
package top.r3944realms.ltdmanager.napcat.event.group

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

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
