
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetFriendMsgHistory请求
 */
@Serializable
data class GetFriendMsgHistoryRequest(
    /**
     * 数量
     */
    val count: Double? = null,

    /**
     * 0为最新
     */
    @SerialName("message_seq")
    val messageSeq: ID? = null,

    /**
     * 倒序
     */
    val reverseOrder: Boolean? = null,

    @SerialName("user_id")
    val userId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_friend_msg_history"
}
