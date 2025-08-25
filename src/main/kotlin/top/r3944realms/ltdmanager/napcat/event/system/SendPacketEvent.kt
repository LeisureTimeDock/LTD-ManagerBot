
package top.r3944realms.ltdmanager.napcat.event.system

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
* SendPacket事件
* @property data 响应数据
*/
@Developing
@Serializable
class SendPacketEvent: AbstractSystemEvent() {
    override fun subtype(): String {
        return "send_packet"
    }
}
    