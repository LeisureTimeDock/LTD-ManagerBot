
package top.r3944realms.ltdmanager.napcat.event.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

/**
 * GetMiniAppArk事件
 * @property data 响应数据
 */
@Serializable
data class GetMiniAppArkEvent(
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
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    /**
     * rawArkData = true
     *
     * rawArkData = false
     */
    @Serializable
    data class Data (
        val appName: String? = null,
        val appView: String? = null,
        val config: Config,
        val desc: String? = null,
        val metaData: MetaData? = null,
        val prompt: String,
        val ver: String,
        val app: String? = null,
        val meta: Meta? = null,
        val miniappOpenRefer: String? = null,
        val miniappShareOrigin: Double? = null,
        val view: String? = null
    )

    @Serializable
    data class Config (
        val autoSize: Double,
        val ctime: Double,
        val forward: Double,
        val height: Double,
        val token: String,
        val type: String,
        val width: Double
    )

    @Serializable
    data class Meta (
        @SerialName("detail_1")
        val detail1: MetaDetail1
    )

    @Serializable
    data class MetaDetail1 (
        val appid: String,
        val appType: Double,
        val desc: String,
        val gamePoints: String,

        @SerialName("gamePointsUrl")
        val gamePointsURL: String,

        val host: PurpleHost,
        val icon: String,
        val preview: String,
        val scene: Double,
        val shareOrigin: Double,
        val shareTemplateData: JsonObject,

        @SerialName("shareTemplateId")
        val shareTemplateID: String,

        val showLittleTail: String,
        val title: String,
        val url: String
    )

    @Serializable
    data class PurpleHost (
        val nick: String,
        val uin: Double
    )

    @Serializable
    data class MetaData (
        @SerialName("detail_1")
        val detail1: MetaDataDetail1
    )

    @Serializable
    data class MetaDataDetail1 (
        val appid: String,
        val appType: Double,
        val desc: String,
        val gamePoints: String,

        @SerialName("gamePointsUrl")
        val gamePointsURL: String,

        val host: FluffyHost,
        val icon: String,
        val preview: String,
        val scene: Double,
        val shareOrigin: Double,
        val shareTemplateData: JsonObject,

        @SerialName("shareTemplateId")
        val shareTemplateID: String,

        val showLittleTail: String,
        val title: String,
        val url: String
    )

    @Serializable
    data class FluffyHost (
        val nick: String,
        val uin: Double
    )
    override fun subtype(): String {
        return "get_mini_app_ark"
    }
}
