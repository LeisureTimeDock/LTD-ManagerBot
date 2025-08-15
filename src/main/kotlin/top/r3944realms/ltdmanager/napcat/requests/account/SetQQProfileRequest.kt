package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.Sex
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

/**
 * QQ设置个人资料事件请求
 * @property nickname 昵称
 * @property personalNote 个性签名(可空)
 * @property sex 性别(可空)
 */
@Serializable
data class SetQQProfileRequest(
    /**
     * 昵称
     */
    val nickname: String,

    /**
     * 个性签名
     */
    @SerialName("personal_note")
    val personalNote: String? = null,

    /**
     * 性别
     */
    val sex: Sex? = null
) : AbstractAccountRequest() {


    override fun toJSON(): String {
        return Json.encodeToString(this)
    }
    override fun path(): String {
        return "/set_qq_profile"
    }

}