
package top.r3944realms.ltdmanager.napcat.event.message.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.event.group.AbstractGroupEvent

/**
 * SendGroupForwardMsg事件
 * @property data 响应数据
 */
@Serializable
data class SendGroupForwardMsgEvent(
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

    val data: Data
    ) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data(
        @SerialName("message_id")
        val messageId: Double,
        @SerialName("res_id")
        val resId:String
    )
    override fun subtype(): String {
        return "send_group_forward_msg"
    }
}
