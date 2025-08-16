
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

/**
 * SetGroupSign事件
 */
@Serializable
class SetGroupSignEvent : NapCatEvent() {
    override fun type(): String {
        return "group/" + subtype()
    }
    override fun subtype(): String {
        return "set_group_sign"
    }
}
