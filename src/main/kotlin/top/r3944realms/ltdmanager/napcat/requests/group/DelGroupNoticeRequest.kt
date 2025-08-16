
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * DelGroupNotice请求
 */
@Serializable
data class DelGroupNoticeRequest(
    @SerialName("group_id")
    val groupId: ID,

    @SerialName("notice_id")
    val noticeId: String
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/_del_group_notice"
}
