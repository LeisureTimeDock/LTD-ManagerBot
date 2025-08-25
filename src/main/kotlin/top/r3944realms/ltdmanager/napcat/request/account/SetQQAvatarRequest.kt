
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SetQqAvatar请求
 */
@Serializable
data class SetQQAvatarRequest(
   val file:String
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_qq_avatar"
}
