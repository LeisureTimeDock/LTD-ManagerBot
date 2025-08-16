
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SendGroupAiRecord请求
 */
@Serializable
data class SendGroupAiRecordRequest(
    /**
     * character_id
     */
    val character: String,

    @SerialName("group_id")
    val groupId: ID,

    /**
     * 文本
     */
    val text: String
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_group_ai_record"
}
