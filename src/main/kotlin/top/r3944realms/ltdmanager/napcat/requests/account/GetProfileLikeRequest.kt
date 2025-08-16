
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetProfileLike请求
 */
@Serializable
data class GetProfileLikeRequest(
    val count: Double? = null,
    val start: Double? = null,

    /**
     * 指定用户，不填为获取所有
     */
    @SerialName("user_id")
    val userId: ID? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_profile_like"
}
