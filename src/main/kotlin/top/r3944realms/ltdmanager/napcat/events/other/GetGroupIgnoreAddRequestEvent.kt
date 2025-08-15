
package top.r3944realms.ltdmanager.napcat.events.other

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetGroupIgnoreAddRequest事件
 * @property data 响应数据
 */
@Developing
@Serializable
data class GetGroupIgnoreAddRequestEvent(
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

    val data: List<Datum>
) : AbstractOtherEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        val actor: Double,
        val checked: Boolean,

        @SerialName("group_id")
        val groupID: Double? = null,

        @SerialName("group_name")
        val groupName: String? = null,

        @SerialName("invitor_nick")
        val invitorNick: String? = null,

        @SerialName("invitor_uin")
        val invitorUin: Double,

        val message: String? = null,

        @SerialName("request_id")
        val requestID: Double,

        @SerialName("requester_nick")
        val requesterNick: String? = null
    )
    override fun subtype(): String {
        return "get_group_ignore_add_request"
    }
}
