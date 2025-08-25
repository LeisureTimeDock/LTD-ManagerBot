
package top.r3944realms.ltdmanager.napcat.request.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetMsgEmojiLike请求
 */
@Serializable
data class SetMsgEmojiLikeRequest(
    /**
     * 表情ID
     */
    @SerialName("emoji_id")
    val emojiId: Double,

    @SerialName("message_id")
    val messageId: ID,

    /**
     * 是否贴
     */
    val set: Boolean
) : AbstractMessageRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_msg_emoji_like"
}
