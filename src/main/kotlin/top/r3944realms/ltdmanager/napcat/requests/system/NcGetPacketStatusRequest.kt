
package top.r3944realms.ltdmanager.napcat.requests.system

import kotlinx.serialization.Serializable

/**
 * NcGetPacketStatus请求
 */
@Serializable
class NcGetPacketStatusRequest : AbstractSystemRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/nc_get_packet_status"
}
