
package top.r3944realms.ltdmanager.napcat.event.passkey

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetRkey事件
 * @property data 响应数据
 */
@Serializable
data class GetRkeyEvent(
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
        @SerialName("created_at")
        val createdAt: Double,

        val rkey: String,
        val ttl: String,
        val type: String
    )
    override fun subtype(): String {
        return "get_rkey"
    }
}
