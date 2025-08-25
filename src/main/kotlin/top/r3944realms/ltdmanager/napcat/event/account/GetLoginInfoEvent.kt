
package top.r3944realms.ltdmanager.napcat.event.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetLoginInfo事件
 * @property data 响应数据
 */
@Serializable
data class GetLoginInfoEvent(
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
    data class Data(
        @SerialName("user_id")
        val userId: Double,
        val nickname: String
    )
    override fun subtype(): String {
        return "get_login_info"
    }
}
