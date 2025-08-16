
package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.Serializable

/**
 * CanSendRecord请求
 */
@Serializable
class CanSendRecordRequest : AbstractPersonalRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/can_send_record"
}
