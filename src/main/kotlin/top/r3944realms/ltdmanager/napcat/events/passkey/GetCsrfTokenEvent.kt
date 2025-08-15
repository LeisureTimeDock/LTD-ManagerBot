
package top.r3944realms.ltdmanager.napcat.events.passkey

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetCsrfToken事件
 * @property data 响应数据
 */
@Serializable
data class GetCsrfTokenEvent(
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
        val cookies: String,
        val token: Double
    )
    override fun subtype(): String {
        return "get_csrf_token"
    }
}
