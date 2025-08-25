package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 获取推荐群聊卡片
 */
@Serializable
data class ArkShareGroupRequest(
    @SerialName("group_id")
    val groupId: String
): AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/ArkShareGroup"
}