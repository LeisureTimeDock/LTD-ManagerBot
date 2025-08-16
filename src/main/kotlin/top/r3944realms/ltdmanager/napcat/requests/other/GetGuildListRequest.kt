
package top.r3944realms.ltdmanager.napcat.requests.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
