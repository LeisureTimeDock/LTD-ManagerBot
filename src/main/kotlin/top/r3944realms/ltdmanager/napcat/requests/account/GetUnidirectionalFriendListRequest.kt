
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetUnidirectionalFriendList请求
 */
@Developing
@Serializable
class GetUnidirectionalFriendListRequest: AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_unidirectional_friend_list"
}
