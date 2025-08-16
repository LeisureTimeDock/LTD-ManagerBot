
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetMsg请求
 */
@Serializable
data class GetMsgRequest(
    @SerialName("message_id")
    val messageId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_msg"
}
