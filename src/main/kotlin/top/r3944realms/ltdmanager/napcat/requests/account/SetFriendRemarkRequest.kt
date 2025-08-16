
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetFriendRemark请求
 */
@Serializable
data class SetFriendRemarkRequest(
    /**
     * 备注名
     */
    val remark: String,

    @SerialName("user_id")
    val userID: ID
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_friend_remark"
}
