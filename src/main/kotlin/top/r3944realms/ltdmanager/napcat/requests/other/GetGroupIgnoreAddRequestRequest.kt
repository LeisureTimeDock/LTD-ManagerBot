
package top.r3944realms.ltdmanager.napcat.requests.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetGroupIgnoreAdd请求
 */
@Developing
@Serializable
class GetGroupIgnoreAddRequestRequest : AbstractOtherRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_group_ignore_add_request"
}
