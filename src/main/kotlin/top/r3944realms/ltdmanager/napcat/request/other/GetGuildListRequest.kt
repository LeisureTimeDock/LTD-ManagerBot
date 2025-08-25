
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetGuildList请求
 */
@Developing
@Serializable
class GetGuildListRequest : AbstractOtherRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_guild_list"
}
