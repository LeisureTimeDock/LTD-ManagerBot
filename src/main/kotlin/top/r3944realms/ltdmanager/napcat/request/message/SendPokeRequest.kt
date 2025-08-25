
package top.r3944realms.ltdmanager.napcat.request.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SendPoke请求
 */
@Serializable
data class SendPokeRequest(
    /**
     * 不填则为私聊戳
     */
    @SerialName("group_id")
    val groupId: ID? = null,

    /**
     * 戳一戳对象
     */
    @SerialName("target_id")
    val targetId: String? = null,

    /**
     * 必填
     */
    @SerialName("user_id")
    val userId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/send_poke"
}
