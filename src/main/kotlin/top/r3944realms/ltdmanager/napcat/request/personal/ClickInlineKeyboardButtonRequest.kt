
package top.r3944realms.ltdmanager.napcat.request.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * ClickInlineKeyboardButton请求
 */
@Serializable
data class ClickInlineKeyboardButtonRequest(
    @SerialName("bot_appid")
    val botAppid: String,

    @SerialName("button_id")
    val buttonId: String,

    @SerialName("callback_data")
    val callbackData: String,

    @SerialName("group_id")
    val groupId: ID,

    @SerialName("msg_seq")
    val msgSeq: String
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/click_inline_keyboard_button"
}
