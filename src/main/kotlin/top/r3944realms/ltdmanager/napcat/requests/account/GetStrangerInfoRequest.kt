
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetStrangerInfo请求
 */
@Serializable
data class GetStrangerInfoRequest(
    @SerialName("user_id")
    val userId: ID
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_stranger_info"
}
