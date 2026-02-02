package top.r3944realms.ltdmanager.mcms.request.instance

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.mcms.request.MCSMRequest
import top.r3944realms.ltdmanager.mcms.response.FailedMCSMResponse
import top.r3944realms.ltdmanager.mcms.response.ResponseResult
import top.r3944realms.ltdmanager.mcms.response.instance.StartInstanceResponse

/**
 * 启动实例请求
 * GET /api/protected_instance/open
 */
@Serializable
data class StartInstanceRequest(
    val uuid: String,
    val daemonId: String
) : MCSMRequest<StartInstanceResponse, FailedMCSMResponse>() {

    override fun toJSON(): String =
        Json.encodeToString(this)

    override fun path(): String =
        "protected_instance/open"

    override fun queryParameters(): Map<String, String> =
        mapOf(
            "uuid" to uuid,
            "daemonId" to daemonId
        )

    override fun method(): HttpMethod = HttpMethod.Get

    override fun getResponse(
        responseJson: String,
        httpStatusCode: HttpStatusCode
    ): ResponseResult<StartInstanceResponse, FailedMCSMResponse> {

        return if (httpStatusCode.value == 200) {
            val obj = Json.decodeFromString(StartInstanceResponse.serializer(), responseJson)
            ResponseResult.Success(obj)
        } else {
            val fail = Json.decodeFromString(FailedMCSMResponse.serializer(), responseJson)
            ResponseResult.Failure(fail)
        }
    }

    override fun expectedResponseType(): String = "StartInstanceResponse"

    override fun expectedFailureType(): String = "FailedMCSMResponse"
}
