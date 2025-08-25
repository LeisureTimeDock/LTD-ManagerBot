
package top.r3944realms.ltdmanager.napcat.event.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupHonorInfo事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupHonorInfoEvent(
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
        /**
         * 当前龙王
         */
        @SerialName("current_talkative")
        val currentTalkative: GroupHonorInfo,

        /**
         * 快乐源泉
         */
        @SerialName("emotion_list")
        val emotionList: List<GroupHonorInfo>,

        @SerialName("group_id")
        val groupId: String,

        /**
         * 龙王
         */
        @SerialName("legend_list")
        val legendList: List<GroupHonorInfo>,

        /**
         * 群聊炽焰
         */
        @SerialName("performer_list")
        val performerList: List<GroupHonorInfo>,

        /**
         * 冒尖小春笋
         */
        @SerialName("strong_newbie_list")
        val strongNewbieList: List<GroupHonorInfo>,

        /**
         * 群聊之火
         */
        @SerialName("talkative_list")
        val talkativeList: List<GroupHonorInfo>
    )

    /**
     * 当前龙王
     *
     * 群荣誉信息
     */
    @Serializable
    data class GroupHonorInfo (
        val avatar: Double? = null,

        /**
         * 说明
         */
        val description: String? = null,

        val nickname: String? = null,

        @SerialName("user_id")
        val userId: Double? = null
    )
    override fun subtype(): String {
        return "get_group_honor_info"
    }
}
