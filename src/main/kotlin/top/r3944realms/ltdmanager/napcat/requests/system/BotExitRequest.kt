
package top.r3944realms.ltdmanager.napcat.requests.system

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * BotExit请求
 */
@Serializable
class BotExitRequest : AbstractSystemRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/bot_exit"
}
