package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * 设置消息已读
 */
@Developing
@Serializable
class GetOnlineClientRequest
    : AbstractAccountRequest(){
    override fun toJSON(): String {
        return "{}"
    }

    override fun path(): String {
        return "/get_online_clients"
    }
}