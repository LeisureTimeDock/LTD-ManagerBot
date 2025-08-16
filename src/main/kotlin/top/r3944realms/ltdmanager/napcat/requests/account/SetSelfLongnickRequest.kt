
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SetSelfLongnick请求
 */
@Serializable
data class SetSelfLongnickRequest(
    /**
     * 内容
     */
    val longNick: String
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_self_longnick"
}
