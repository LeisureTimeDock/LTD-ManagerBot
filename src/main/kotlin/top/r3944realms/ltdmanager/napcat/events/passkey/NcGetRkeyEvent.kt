
package top.r3944realms.ltdmanager.napcat.events.passkey

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * NcGetRkey事件
 * @property data 响应数据
 */
@Serializable
data class NcGetRkeyEvent(
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
) : AbstractPassKeyEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        val rkey: String,
        val time: Double,
        val ttl: String,
        val type: Double
    )
    override fun subtype(): String {
        return "nc_get_rkey"
    }
}
