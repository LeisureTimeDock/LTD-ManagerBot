package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 获取推荐好友/群聊卡片事件
 * @property data 响应数据
 */
@Serializable
data class ArkSharePeerEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: Data
): AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data(
        val errCode: Int,
        val errMsg: String,
        /**
         * 卡片json
         */
        @SerialName("arkJson")
        val arkJSON: String,
    )

    override fun subtype(): String {
        return "ArkSharePeer"
    }
}