
package top.r3944realms.ltdmanager.napcat.events.system

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
* GetVersionInfo事件
* @property status 状态字符串
* @property retcode 返回代码
* @property message 消息
* @property wording 文字描述
* @property echo 回显字段 (可空)
* @property data 响应数据
*/
@Serializable
data class GetVersionInfoEvent(
    /**
     * 状态字符串
     */
    val status: Status,
    /**
     * 返回代码
     */
    val retcode: Double,
    /**
     * 消息
     */
    val message: String,
    /**
     * 文字描述
     */
    val wording: String,
    /**
     * 回显字段
     */
    val echo: String? = null,

    val data: Data
) : AbstractSystemEvent() {
    @Serializable
    data class Data (
        @SerialName("app_name")
        val appName: String,

        @SerialName("app_version")
        val appVersion: String,

        @SerialName("protocol_version")
        val protocolVersion: String
    )
    override fun subtype(): String {
        return "get_version_info"
    }
}
    