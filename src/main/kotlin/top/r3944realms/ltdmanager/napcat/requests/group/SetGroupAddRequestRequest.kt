
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupAdd请求
 */
@Serializable
data class SetGroupAddRequestRequest(
    /**
     * 是否同意
     */
    val approve: Boolean,

    /**
     * 请求id
     */
    val flag: String,

    /**
     * 拒绝理由
     */
    val reason: String? = null
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_add_request"
}
