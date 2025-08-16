
package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * GetRecord请求
 */
@Serializable
data class GetRecordRequest(
    val file: String? = null,

    @SerialName("file_id")
    val fileId: String? = null,

    /**
     * 输出格式
     */
    @SerialName("out_format")
    val outFormat: OutFormat
) : AbstractMessageRequest() {
    /**
     * 输出格式
     */
    @Serializable
    enum class OutFormat(val value: String) {
        @SerialName("amr") AMR("amr"),
        @SerialName("flac") FLAC("flac"),
        @SerialName("m4a") M4A("m4a"),
        @SerialName("mp3") Mp3("mp3"),
        @SerialName("ogg") Ogg("ogg"),
        @SerialName("spx") Spx("spx"),
        @SerialName("wma") WMA("wma"),
        @SerialName("wav") Wav("wav");
    }
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_record"
}
