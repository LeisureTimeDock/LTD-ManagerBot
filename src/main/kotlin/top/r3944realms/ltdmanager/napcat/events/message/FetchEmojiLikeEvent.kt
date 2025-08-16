
package top.r3944realms.ltdmanager.napcat.events.message

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.events.group.AbstractGroupEvent

/**
 * FetchEmojiLike事件
 * @property data 响应数据
 */
@Serializable
data class FetchEmojiLikeEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: Data
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data(
        val result: Double,
        val errMsg: String? = null,
    )
    override fun subtype(): String {
        return "fetch_emoji_like"
    }
}
