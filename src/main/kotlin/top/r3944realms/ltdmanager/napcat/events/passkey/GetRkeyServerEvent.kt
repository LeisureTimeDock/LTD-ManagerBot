
package top.r3944realms.ltdmanager.napcat.events.passkey

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetRkeyServer事件
 * @property data 响应数据
 */
@Serializable
data class GetRkeyServerEvent(
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
) : AbstractPassKeyEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        @SerialName("expired_time")
        val expiredTime: Double,

        @SerialName("group_rkey")
        val groupRkey: String,

        val name: String,

        @SerialName("private_rkey")
        val privateRkey: String
    )
    override fun subtype(): String {
        return "get_rkey_server"
    }
}
