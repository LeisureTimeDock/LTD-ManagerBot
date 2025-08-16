
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * MarkAllAsRead请求
 */
@Serializable
class MarkAllAsReadRequest: AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/_mark_all_as_read"
}
