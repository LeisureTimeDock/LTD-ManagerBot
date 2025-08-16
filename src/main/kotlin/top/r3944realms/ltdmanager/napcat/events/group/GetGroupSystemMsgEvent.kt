
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupSystemMsg事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupSystemMsgEvent(
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
        @SerialName("InvitedRequest")
        val invitedRequest: List<SystemInfo>,

        @SerialName("join_requests")
        val joinRequests: List<SystemInfo>
    )

    /**
     * 系统信息
     */
    @Serializable
    data class SystemInfo (
        val actor: Double,
        val checked: Boolean,

        @SerialName("group_id")
        val groupID: Double,

        @SerialName("group_name")
        val groupName: String,

        @SerialName("invitor_nick")
        val invitorNick: String,

        @SerialName("invitor_uin")
        val invitorUin: Double,

        val message: String,

        @SerialName("request_id")
        val requestID: Double,

        @SerialName("requester_nick")
        val requesterNick: String
    )
    override fun subtype(): String {
        return "get_group_system_msg"
    }
}
