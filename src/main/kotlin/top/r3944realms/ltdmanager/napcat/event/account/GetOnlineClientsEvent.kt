package top.r3944realms.ltdmanager.napcat.event.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * 获取当前账号在线客户端列表
 */
@Developing
@Serializable
data class GetOnlineClientsEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: List<String>,
): AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    override fun subtype(): String {
        return "get_online_clients"
    }
}