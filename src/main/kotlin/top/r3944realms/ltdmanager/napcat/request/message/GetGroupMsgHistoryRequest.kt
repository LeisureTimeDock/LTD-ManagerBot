
package top.r3944realms.ltdmanager.napcat.request.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupMsgHistory请求
 */
@Serializable
data class GetGroupMsgHistoryRequest(
    /**
     * 数量
     */
    val count: Int? = null,

    @SerialName("group_id")
    val groupId: ID,

    /**
     * 0为最新
     */
    @SerialName("message_seq")
    val messageSeq: ID? = null,

    /**
     * 倒序
     */
    val reverseOrder: Boolean? = null
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_msg_history"
}
