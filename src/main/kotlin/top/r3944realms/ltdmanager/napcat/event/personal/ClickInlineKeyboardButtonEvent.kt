
package top.r3944realms.ltdmanager.napcat.event.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * ClickInlineKeyboardButton事件
 * @property data 响应数据
 */
@Serializable
data class ClickInlineKeyboardButtonEvent(
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
) : AbstractPersonalEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val errMsg: String,
        val promptIcon: Double,
        val promptText: String,
        val promptType: Double,
        val result: Double,
        val status: Double
    )

    override fun subtype(): String {
        return "click_inline_keyboard_button"
    }
}
