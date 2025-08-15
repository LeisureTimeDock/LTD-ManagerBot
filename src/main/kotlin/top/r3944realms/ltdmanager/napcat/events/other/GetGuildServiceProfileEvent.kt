
package top.r3944realms.ltdmanager.napcat.events.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import top.r3944realms.ltdmanager.napcat.Developing
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

/**
 * GetGuildServiceProfile事件
 */
@Developing
@Serializable
class GetGuildServiceProfileEvent: NapCatEvent() {
    override fun type(): String {
        return "other/" + subtype()
    }

    override fun subtype(): String {
        return "get_guild_list"
    }
}
