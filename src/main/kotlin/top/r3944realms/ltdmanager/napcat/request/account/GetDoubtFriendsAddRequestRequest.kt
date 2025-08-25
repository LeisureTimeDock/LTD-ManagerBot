package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 获取被过滤好友请求
 */
@Serializable
data class GetDoubtFriendsAddRequestRequest(
    val count: Int = 50
) : AbstractAccountRequest() {
    override fun toJSON(): String {
        return Json.encodeToString(this)
    }

    override fun path(): String {
        return "/get_doubt_friends_add_request"
    }

}