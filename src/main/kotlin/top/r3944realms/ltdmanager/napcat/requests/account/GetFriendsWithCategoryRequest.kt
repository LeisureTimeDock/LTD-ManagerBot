
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetFriendsWithCategory请求
 */
@Serializable
class GetFriendsWithCategoryRequest : AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_friends_with_category"
}
