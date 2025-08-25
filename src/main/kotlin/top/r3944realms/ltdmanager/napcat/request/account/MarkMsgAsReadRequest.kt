package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * 设置消息已读
 */
@Serializable
data class MarkMsgAsReadRequest (
    /**
     * 与user_id二选一
     */
    @SerialName("group_id")
    val groupId: ID? = null,

    /**
     * 与group_id二选一
     */
    @SerialName("user_id")
    val userId: ID? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String {
        return Json.encodeToString(this)
    }

    override fun path(): String {
        return "/mark_msg_as_read"
    }


}
