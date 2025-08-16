
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupDetailInfo事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupDetailInfoEvent(
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
        @SerialName("group_all_shut")
        val groupAllShut: Double,

        @SerialName("group_id")
        val groupID: Double,

        @SerialName("group_name")
        val groupName: String,

        @SerialName("group_remark")
        val groupRemark: String,

        @SerialName("max_member_count")
        val maxMemberCount: Double,

        @SerialName("member_count")
        val memberCount: Double
    )
    override fun subtype(): String {
        return "get_group_detail_info"
    }
}
