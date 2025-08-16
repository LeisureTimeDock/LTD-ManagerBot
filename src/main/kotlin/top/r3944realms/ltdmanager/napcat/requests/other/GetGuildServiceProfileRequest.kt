
package top.r3944realms.ltdmanager.napcat.requests.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetGuildServiceProfile请求
 */
@Developing
@Serializable
class GetGuildServiceProfileRequest: AbstractOtherRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_guild_service_profile"
}
