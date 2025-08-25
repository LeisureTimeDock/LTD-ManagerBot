
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable

/**
 * GetFriendList请求
 */
@Serializable
class GetFriendListRequest: AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_friend_list"
}
