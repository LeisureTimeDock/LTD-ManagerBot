
package top.r3944realms.ltdmanager.napcat.event.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetGroupInfoEx事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupInfoExEvent(
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
        val extInfo: EXTInfo,
        val groupCode: String,
        val resultCode: Double
    )

    @Serializable
    data class EXTInfo (
        @SerialName("bindGuildId")
        val bindGuildID: String,

        val blacklistExpireTime: Double,

        @SerialName("companyId")
        val companyID: Double,

        val essentialMsgPrivilege: Double,
        val essentialMsgSwitch: Double,
        val fullGroupExpansionSeq: String,
        val fullGroupExpansionSwitch: Double,

        @SerialName("gangUpId")
        val gangUpID: String,

        @SerialName("groupAioBindGuildId")
        val groupAioBindGuildID: String,

        @SerialName("groupBindGuildIds")
        val groupBindGuildIDS: GroupBindGuildIDS,

        val groupBindGuildSwitch: Double,

        @SerialName("groupExcludeGuildIds")
        val groupExcludeGuildIDS: GroupExcludeGuildIDS,

        @SerialName("groupExtFlameData")
        val groupEXTFlameData: GroupEXTFlameData,

        val groupFlagPro1: String,

        @SerialName("groupInfoExtSeq")
        val groupInfoEXTSeq: Double,

        @SerialName("groupOwnerId")
        val groupOwnerID: GroupOwnerID,

        val groupSquareSwitch: Double,
        val hasGroupCustomPortrait: Double,
        val inviteRobotMemberExamine: Double,
        val inviteRobotMemberSwitch: Double,
        val inviteRobotSwitch: Double,

        @SerialName("isLimitGroupRtc")
        val isLimitGroupRTC: Double,

        val lightCharNum: Double,
        val luckyWord: String,

        @SerialName("luckyWordId")
        val luckyWordID: String,

        val msgEventSeq: String,
        val qqMusicMedalSwitch: Double,
        val reserve: Double,
        val showPlayTogetherSwitch: Double,

        @SerialName("starId")
        val starID: Double,

        val todoSeq: Double,
        val viewedMsgDisappearTime: String
    )

    @Serializable
    data class GroupBindGuildIDS (
        @SerialName("guildIds")
        val guildIDS: List<String>
    )

    @Serializable
    data class GroupEXTFlameData (
        val dayNums: List<String>,
        val isDisplayDayNum: Boolean,
        val state: Long,
        val switchState: Long,
        val updateTime: String,
        val version: Long
    )

    @Serializable
    data class GroupExcludeGuildIDS (
        @SerialName("guildIds")
        val guildIDS: List<String>
    )

    @Serializable
    data class GroupOwnerID (
        val memberQid: String,
        val memberUid: String,
        val memberUin: String
    )
    override fun subtype(): String {
        return "get_group_info_ex"
    }
}
