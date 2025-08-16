
package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * SetDiyOnlineStatus事件
 * @property data 响应数据
 */
@Serializable
data class SetDiyOnlineStatusEvent(
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

    val data: String
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    
    override fun subtype(): String {
        return "set_diy_online_status"
    }
}
