
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupIgnoredNotifies事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupIgnoredNotifiesEvent(
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

    val data: GetGroupSystemMsgEvent.Data
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {

    override fun subtype(): String {
        return "get_group_ignored_notifies"
    }
}
