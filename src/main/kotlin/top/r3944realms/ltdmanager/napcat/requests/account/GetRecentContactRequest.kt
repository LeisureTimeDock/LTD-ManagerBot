
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetRecentContact请求
 */
@Serializable
data class GetRecentContactRequest(
    /**
     * 会话数量
     */
    val count: Double? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_recent_contact"
}
