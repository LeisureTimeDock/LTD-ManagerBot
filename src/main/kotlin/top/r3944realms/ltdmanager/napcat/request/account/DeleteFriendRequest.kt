
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * DeleteFriend请求
 */
@Serializable
data class DeleteFriendRequest(
    /**
     * 同 user_id
     */
    @SerialName("friend_id")
    val friendId: ID? = null,

    /**
     * 拉黑
     */
    @SerialName("temp_block")
    val tempBlock: Boolean,

    /**
     * 双向删除
     */
    @SerialName("temp_both_del")
    val tempBothDel: Boolean,

    @SerialName("user_id")
    val userId: ID? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/delete_friend"
}
