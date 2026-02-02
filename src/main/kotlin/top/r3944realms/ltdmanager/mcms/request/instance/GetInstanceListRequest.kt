package top.r3944realms.ltdmanager.mcms.request.instance

import io.ktor.http.*
import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.mcms.request.MCSMRequest
import top.r3944realms.ltdmanager.mcms.response.FailedMCSMResponse
import top.r3944realms.ltdmanager.mcms.response.ResponseResult
import top.r3944realms.ltdmanager.mcms.response.instance.GetInstanceListResponse

@Serializable
class GetInstanceListRequest(
    private val daemonId: String,
    private val page: Int,
    private val pageSize: Int,
    private val status: String,
    private val instanceName: String? = null
) : MCSMRequest<GetInstanceListResponse, FailedMCSMResponse>() {

    override fun toJSON(): String = "{}"  // GET 无请求体

    override fun path(): String = "api/service/remote_service_instances"

    override fun queryParameters(): Map<String, String> =
        buildMap {
            put("daemonId", daemonId)
            put("page", page.toString())
            put("page_size", pageSize.toString())
            put("status", status)
            instanceName?.let { put("instance_name", it) }
        }

    override fun getResponse(
        responseJson: String,
        httpStatusCode: HttpStatusCode
    ): ResponseResult<GetInstanceListResponse, FailedMCSMResponse> {
        return if (httpStatusCode.value in 200..299) {
            ResponseResult.Success(
                kotlinx.serialization.json.Json.decodeFromString<GetInstanceListResponse>(responseJson)
            )
        } else {
            ResponseResult.Failure(
                kotlinx.serialization.json.Json.decodeFromString<FailedMCSMResponse>(responseJson)
            )
        }
    }

    override fun expectedResponseType(): String = GetInstanceListResponse::class.simpleName!!

    override fun expectedFailureType(): String = FailedMCSMResponse::class.simpleName!!
}
