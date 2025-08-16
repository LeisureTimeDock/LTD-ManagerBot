
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetInputStatus请求
 */
@Serializable
data class SetInputStatusRequest(
    @SerialName("event_type")
    val eventType: EventType,

    @SerialName("user_id")
    val userId: ID
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_input_status"
    @Serializable
    enum class EventType(val value: Int) {
        @SerialName("0")SPEAKING(0),
        @SerialName("1")TYPING(1)
    }
}
