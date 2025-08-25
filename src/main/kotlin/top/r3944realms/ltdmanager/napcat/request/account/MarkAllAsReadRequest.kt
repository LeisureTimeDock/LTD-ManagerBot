
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable

/**
 * MarkAllAsRead请求
 */
@Serializable
class MarkAllAsReadRequest: AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/_mark_all_as_read"
}
