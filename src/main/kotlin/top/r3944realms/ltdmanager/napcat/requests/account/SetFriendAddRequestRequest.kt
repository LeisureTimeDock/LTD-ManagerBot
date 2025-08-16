
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SetFriendAdd请求
 */
@Serializable
data class SetFriendAddRequestRequest(
    /**
     * 是否同意
     */
    val approve: Boolean,

    /**
     * 请求id
     */
    val flag: String,

    /**
     * 好友备注
     */
    val remark: String
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_friend_add_request"
}
