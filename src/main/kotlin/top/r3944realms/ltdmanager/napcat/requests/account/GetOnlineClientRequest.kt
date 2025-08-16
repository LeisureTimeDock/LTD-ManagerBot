package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * 获取当前账号在线客户端列表
 */
@Developing
@Serializable
class GetOnlineClientRequest : AbstractAccountRequest(){
    override fun toJSON(): String = "{}"


    override fun path(): String {
        return "/get_online_clients"
    }
}