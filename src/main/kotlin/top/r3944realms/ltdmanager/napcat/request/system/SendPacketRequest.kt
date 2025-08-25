
package top.r3944realms.ltdmanager.napcat.request.system

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * SendPacket请求
 */
@Developing
@Serializable
class SendPacketRequest : AbstractSystemRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/send_packet"
}
