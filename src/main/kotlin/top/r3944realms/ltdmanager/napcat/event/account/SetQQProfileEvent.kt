package top.r3944realms.ltdmanager.napcat.event.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * QQ设置个人资料事件响应
 * @property data 响应数据
 */
@Serializable
data class SetQQProfileEvent(
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
    /**
     * 响应数据
     */
    val data: Data,
    ): AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    /**
     * 响应数据
     * @property result 相关数字
     * @property errorMsg 错误信息(成功时为null)
     */
    @Serializable
    data class Data(
        /**
         * 相关数字
         */
        @SerialName("result")
        val result: Double,
        /**
         * 错误信息
         */
        @SerialName("errMsg")
        val errorMsg: String? = null
    )


    val isSuccess: Boolean get() = status == Status.Ok && retcode == 0.0

    override fun subtype(): String {
        return "set_qq_profile"
    }

}