
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupAtAllRemain事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupAtAllRemainEvent(
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
    data class Data (
        @SerialName("can_at_all")
        val canAtAll: Boolean,

        @SerialName("remain_at_all_count_for_group")
        val remainAtAllCountForGroup: Double,

        @SerialName("remain_at_all_count_for_uin")
        val remainAtAllCountForUin: Double
    )
    override fun subtype(): String {
        return "get_group_at_all_remain"
    }
}
