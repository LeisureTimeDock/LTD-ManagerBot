
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.Serializable

/**
 * GetGroupIgnoredNotifies请求
 */
@Serializable
class GetGroupIgnoredNotifiesRequest: AbstractGroupRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_group_ignored_notifies"
}
