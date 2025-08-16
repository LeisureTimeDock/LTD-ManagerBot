
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SendGroupNotice请求
 */
@Serializable
data class SendGroupNoticeRequest(
    @SerialName("confirm_required")
    val confirmRequired: ID? = null,

    /**
     * 内容
     */
    val content: String,

    @SerialName("group_id")
    val groupId: ID,

    /**
     * 图片路径
     */
    val image: String? = null,

    @SerialName("is_show_edit_card")
    val isShowEditCard: ID? = null,

    val pinned: ID? = null,

    @SerialName("tip_window_type")
    val tipWindowType: ID? = null,

    val type: ID? = null
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/_send_group_notice"
}
