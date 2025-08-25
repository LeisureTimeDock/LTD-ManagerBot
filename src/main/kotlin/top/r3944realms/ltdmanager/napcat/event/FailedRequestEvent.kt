package top.r3944realms.ltdmanager.napcat.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class FailedRequestEvent(
    val status: Status = Status.Failed,
    val retcode: Int,
    val data: JsonElement?= null,
    val message: String,
    val wording: String,
    val echo: String? = null
): NapCatEvent() {
    override fun type(): String {
        return "FailedRequestEvent"
    }

    override fun subtype(): String {
        return "FailedRequestEvent"
    }

    override fun isOk(): Boolean = false

    companion object {
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
            }
        }
    }
}