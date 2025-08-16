
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetAiCharacters请求
 */
@Serializable
data class GetAiCharactersRequest(
    /**
     * 1 or 2?
     */
    @SerialName("chat_type")
    val chatType: ID? = null,

    @SerialName("group_id")
    val groupId: ID
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_ai_characters"
}
