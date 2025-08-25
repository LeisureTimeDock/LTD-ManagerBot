
package top.r3944realms.ltdmanager.napcat.event.group

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

/**
 * DeleteEssenceMsg事件
 * @property data 响应数据
 */
@Serializable
data class DeleteEssenceMsgEvent(
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
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val errCode: String,
        val errMsg: String,
        val result: Result
    )

    @Serializable
    data class Result (
        val digestTime: String,
        val digestUin: String,
        val msg: JsonObject,

        /**
         * 正常为空，异常有文本提示
         */
        val wording: String
    )
    override fun subtype(): String {
        return "delete_essence_msg"
    }
}
