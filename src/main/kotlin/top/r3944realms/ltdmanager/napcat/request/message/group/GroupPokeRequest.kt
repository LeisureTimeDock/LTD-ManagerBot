
package top.r3944realms.ltdmanager.napcat.request.message.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.request.message.AbstractMessageRequest

/**
 * GroupPoke请求
 */
@Serializable
data class GroupPokeRequest(
    @SerialName("group_id")
    val groupId: ID,

    @SerialName("user_id")
    val userId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/group_poke"
}
