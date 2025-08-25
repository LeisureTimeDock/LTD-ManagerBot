
package top.r3944realms.ltdmanager.napcat.event.message.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import top.r3944realms.ltdmanager.napcat.event.group.AbstractGroupEvent

/**
 * SendPrivateForwardMsg事件
 * @property data 响应数据
 */
@Serializable
data class SendPrivateForwardMsgEvent(
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

    val data: JsonElement? = null,
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    
    override fun subtype(): String {
        return "send_private_forward_msg"
    }
}
