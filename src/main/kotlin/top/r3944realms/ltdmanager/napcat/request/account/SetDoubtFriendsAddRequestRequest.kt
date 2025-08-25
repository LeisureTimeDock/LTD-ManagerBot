package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 处理被过滤好友请求
 */
@Serializable
data class SetDoubtFriendsAddRequestRequest(
  /**
   * 4.7.43 版本中该值无效
   */
  val approve: Boolean,

  val flag: String
) : AbstractAccountRequest() {
    override fun toJSON(): String {
    return Json.encodeToString(this)
    }

    override fun path(): String {
        return "/set_doubt_friends_add_request"
    }
}