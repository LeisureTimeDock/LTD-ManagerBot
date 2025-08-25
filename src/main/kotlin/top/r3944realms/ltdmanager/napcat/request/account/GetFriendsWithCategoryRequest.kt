
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable

/**
 * GetFriendsWithCategory请求
 */
@Serializable
class GetFriendsWithCategoryRequest : AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_friends_with_category"
}
