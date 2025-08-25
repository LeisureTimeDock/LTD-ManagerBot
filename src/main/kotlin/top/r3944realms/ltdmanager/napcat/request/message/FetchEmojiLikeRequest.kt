
package top.r3944realms.ltdmanager.napcat.request.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * FetchEmojiLike请求
 */
@Serializable
data class FetchEmojiLikeRequest(
    val count: Double? = null,

    /**
     * 表情ID
     */
    @SerialName("emojiId")
    val emojiId: String,

    /**
     * 表情类型
     */
    val emojiType: String,

    @SerialName("message_id")
    val messageId: ID
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/fetch_emoji_like"
}
