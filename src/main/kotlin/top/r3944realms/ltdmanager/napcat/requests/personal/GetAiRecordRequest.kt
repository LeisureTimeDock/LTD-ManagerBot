
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetAiRecord请求
 */
@Serializable
data class GetAiRecordRequest(
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
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_ai_record"
}
