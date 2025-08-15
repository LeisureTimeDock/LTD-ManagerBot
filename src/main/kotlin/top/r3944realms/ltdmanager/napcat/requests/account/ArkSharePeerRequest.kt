package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

/**
 * 获取推荐好友/群聊卡片
 */
@Serializable
data class ArkSharePeerRequest(
    /**
     * 和user_id二选一
     */
    @SerialName("group_id")
    val groupID: ID? = null,

    /**
     * 对方手机号
     */
    val phoneNumber: String? = null,

    /**
     * 和group_id二选一
     */
    @SerialName("user_id")
    val userID: ID? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String {
        return Json.encodeToString(this)
    }

    override fun path(): String {
        return "/ArkSharePeer"
    }
}


