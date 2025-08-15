package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 获取被过滤好友请求响应
 * @property data 响应数据
 */
@Serializable
data class GetDoubtFriendsAddRequestEvent(
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

    val data: List<Datum>,
): AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        val flag: String,
        @SerialName("group_code")
        val groupCode: String,
        val msg: String,
        val nick: String,
        val reason: String,
        val source: String,
        val time: String,
        val type: String,
        val uin: String
    )

    override fun subtype(): String {
        return "get_doubt_friends_add_request"
    }
}