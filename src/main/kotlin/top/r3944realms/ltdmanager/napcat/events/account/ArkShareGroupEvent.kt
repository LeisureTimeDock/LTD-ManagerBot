package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 获取推荐群聊卡片
 */
@Serializable
data class ArkShareGroupEvent(
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
    /**
     * 卡片json
     */
    val data: String
    ) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    override fun subtype(): String {
        return "ArkShareGroup"
    }
}