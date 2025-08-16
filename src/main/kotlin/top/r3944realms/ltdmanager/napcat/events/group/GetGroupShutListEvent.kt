
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

/**
 * GetGroupShutList事件
 * @property data 响应数据
 */
@Serializable
data class GetGroupShutListEvent(
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
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        val autoRemark: String,
        val avatarPath: String,
        val bigClubFlag: Double,
        val bigClubLevel: Double,
        val cardName: String,

        @SerialName("cardNameId")
        val cardNameID: Double,

        val cardType: Double,
        val creditLevel: Double,
        val globalGroupLevel: Double,
        val globalGroupPoint: Double,
        val groupHonor: JsonObject,
        val isDelete: Boolean,
        val isRobot: Boolean,
        val isSpecialConcerned: Boolean,
        val isSpecialShield: Boolean,
        val isSpecialShielded: Boolean,

        /**
         * 入群时间
         */
        val joinTime: Double,

        /**
         * 最后发言时间
         */
        val lastSpeakTime: Double,

        val memberFlag: Double,

        @SerialName("memberFlagExt")
        val memberFlagEXT: Double,

        val memberFlagExt2: Double,
        val memberLevel: Double,
        val memberMobileFlag: Double,

        /**
         * 群聊等级
         */
        val memberRealLevel: Double,

        val memberSpecialTitle: String,

        @SerialName("memberTitleId")
        val memberTitleID: Double,

        val mssVipType: Double,
        val nick: String,
        val qid: String,
        val remark: String,
        val richFlag: Double,
        val role: Double,

        /**
         * 解禁时间
         */
        val shutUpTime: Double,

        val specialTitleExpireTime: String,
        val uid: String,
        val uin: String,
        val userShowFlag: Double,
        val userShowFlagNew: Double
    )
    override fun subtype(): String {
        return "get_group_shut_list"
    }
}
