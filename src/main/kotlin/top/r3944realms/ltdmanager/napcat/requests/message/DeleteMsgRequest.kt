
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.yaml.snakeyaml.events.Event.ID

/**
 * DeleteMsg请求
 */
@Serializable
data class DeleteMsgRequest(
   @SerialName("message_id")
    val messageId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/delete_msg"
}
