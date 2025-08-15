package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SetOnlineStatusRequest(
    /**
     * 电量
     */
    val batteryStatus: Double,

    /**
     * 详情看顶部
     */
    val extStatus: Double,

    /**
     * 详情看顶部
     */
    val status: Double
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_online_status"

}