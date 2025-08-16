
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * SetModelShow请求
 */
@Developing
@Serializable
class SetModelShowRequest : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/_set_model_show"
}
