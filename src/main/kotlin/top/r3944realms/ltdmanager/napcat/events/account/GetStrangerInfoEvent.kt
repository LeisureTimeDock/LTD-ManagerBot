
package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetStrangerInfo事件
 * @property data 响应数据
 */
@Serializable
data class GetStrangerInfoEvent(
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
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        /**
         * 年龄
         */
        val age: Double,

        /**
         * 是否会员
         */
        @SerialName("is_vip")
        val isVip: Boolean,

        /**
         * 是否年费会员
         */
        @SerialName("is_years_vip")
        val isYearsVip: Boolean,

        /**
         * 连续登录天数
         */
        @SerialName("login_days")
        val loginDays: Double,

        /**
         * 个性签名
         */
        @SerialName("long_nick")
        val longNick: String,

        /**
         * 昵称
         */
        val nickname: String,

        val qid: String,

        /**
         * 账号等级
         */
        val qqLevel: Double,

        /**
         * 注册时间
         */
        @SerialName("reg_time")
        val regTime: Double,

        /**
         * 备注
         */
        val remark: String,

        /**
         * 性别
         */
        val sex: String,

        val status: Double,
        val uid: String,
        val uin: String,

        @SerialName("user_id")
        val userID: Double,

        /**
         * 会员等级
         */
        @SerialName("vip_level")
        val vipLevel: Double
    )
    override fun subtype(): String {
        return "get_stranger_info"
    }
}
