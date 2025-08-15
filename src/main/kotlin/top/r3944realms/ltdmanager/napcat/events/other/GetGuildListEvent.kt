
package top.r3944realms.ltdmanager.napcat.events.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

/**
 * GetGuildList事件
 */
@Developing
@Serializable
class GetGuildListEvent: NapCatEvent() {
    override fun type(): String = "other/" + subtype()

    override fun subtype(): String {
        return "get_guild_list"
    }
}
