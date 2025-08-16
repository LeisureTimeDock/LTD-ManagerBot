
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetStatus请求
 */
@Serializable
data class GetStatusRequest(
    /**
     * 表情ID，表情ID
     */
    @SerialName("face_id")
    val faceId: ID,

    @SerialName("face_type")
    val faceType: ID? = null,

    /**
     * 描述文本
     */
    val wording: String? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_status"
}
