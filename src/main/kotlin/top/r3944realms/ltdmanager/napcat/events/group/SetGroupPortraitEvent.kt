
package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * SetGroupPortrait事件
 * @property data 响应数据
 */

@Serializable
data class SetGroupPortraitEvent(
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
    data class Data(
        //TODO: 文档里Data result字段类型存疑(返回响应与示例不一致)，需验证
        // https://napcat.apifox.cn/226658669e0
        val result: String,
        val errMsg: String
    )
    override fun subtype(): String {
        return "set_group_portrait"
    }
}
