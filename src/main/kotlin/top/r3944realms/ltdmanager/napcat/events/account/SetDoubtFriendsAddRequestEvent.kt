package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

/**
 * 处理被过滤好友请求响应
 */
@Serializable
data class SetDoubtFriendsAddRequestEvent (
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
    val data: JsonObject,
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    override fun subtype(): String {
        return "set_doubt_friends_add_request"
    }

}