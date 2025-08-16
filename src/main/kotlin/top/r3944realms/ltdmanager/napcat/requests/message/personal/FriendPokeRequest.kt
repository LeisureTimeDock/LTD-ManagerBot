
package top.r3944realms.ltdmanager.napcat.requests.message.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.requests.message.AbstractMessageRequest

/**
 * FriendPoke请求
 */
@Serializable
data class FriendPokeRequest(
    /**
     * 戳一戳对象，可不填
     */
    @SerialName("target_id")
    val targetID: ID? = null,

    /**
     * 私聊对象
     */
    @SerialName("user_id")
    val userID: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/friend_poke"
}
