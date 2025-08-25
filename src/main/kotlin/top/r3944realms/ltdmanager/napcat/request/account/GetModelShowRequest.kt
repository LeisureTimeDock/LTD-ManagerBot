
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetModelShow请求
 */
@Serializable
data class GetModelShowRequest(
    val model: String
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/_get_model_show"
}
