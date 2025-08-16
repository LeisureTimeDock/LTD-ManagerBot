
package top.r3944realms.ltdmanager.napcat.events.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import top.r3944realms.ltdmanager.napcat.events.group.AbstractGroupEvent

/**
 * SendForwardMsg事件
 * @property data 响应数据
 */
@Serializable
data class SendForwardMsgEvent(
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

    val data: JsonObject
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    
    override fun subtype(): String {
        return "send_forward_msg"
    }
}
