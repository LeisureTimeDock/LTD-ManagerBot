
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetMiniAppArk请求
 */
@Serializable
data class GetMiniAppArkRequest(
    /**
     * 描述
     */
    val desc: String,

    /**
     * 跳转URL
     */
    @SerialName("jumpUrl")
    val jumpURL: String,

    /**
     * 图片URL
     */
    @SerialName("picUrl")
    val picURL: String,

    /**
     * 是否返回原始ark数据
     */
    val rawArkData: Boolean? = null,

    /**
     * 标题
     */
    val title: String,

    /**
     * 类型
     */
    val type: Type? = null,

    /**
     * 网页URL
     */
    @SerialName("webUrl")
    val webURL: String? = null,

    /**
     * 应用ID
     */
    @SerialName("appId")
    val appId: String? = null,

    /**
     * 业务类型
     */
    val businessType: ID? = null,

    /**
     * 图标URL
     */
    @SerialName("iconUrl")
    val iconURL: String? = null,

    /**
     * 场景
     */
    val scene: ID? = null,

    /**
     * SDK ID
     */
    @SerialName("sdkId")
    val sdkId: String? = null,

    /**
     * 分享类型
     */
    val shareType: ID? = null,

    /**
     * 模板类型
     */
    val templateType: ID? = null,

    /**
     * 版本ID
     */
    @SerialName("versionId")
    val versionId: String? = null,

    /**
     * 版本类型
     */
    val verType: ID? = null,

    /**
     * 是否分享
     */
    val withShareTicket: ID? = null
) : AbstractAccountRequest() {

    /**
     * 类型
     */
    @Serializable
    enum class Type(val value: String) {
        @SerialName("bili") Bili("bili"),
        @SerialName("weibo") Weibo("weibo");
    }
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_mini_app_ark"
}
