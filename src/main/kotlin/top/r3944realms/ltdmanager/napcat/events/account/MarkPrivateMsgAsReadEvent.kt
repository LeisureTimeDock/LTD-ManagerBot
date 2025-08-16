
package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

/**
 * MarkPrivateMsgAsRead事件
 * @property data 响应数据
 */
@Serializable
data class MarkPrivateMsgAsReadEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: JsonElement? = null
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    
    override fun subtype(): String {
        return "mark_private_msg_as_read"
    }
}
