
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.Serializable
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
