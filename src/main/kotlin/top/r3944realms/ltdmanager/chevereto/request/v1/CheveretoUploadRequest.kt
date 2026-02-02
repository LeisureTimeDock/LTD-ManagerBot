package top.r3944realms.ltdmanager.chevereto.request.v1

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.chevereto.data.CheveretoSource
import top.r3944realms.ltdmanager.chevereto.request.CheveretoRequest
import top.r3944realms.ltdmanager.chevereto.response.FailedCheveretoResponse
import top.r3944realms.ltdmanager.chevereto.response.v1.CheveretoUploadResponse
import top.r3944realms.ltdmanager.core.client.response.ResponseResult

@Serializable
data class CheveretoUploadRequest(
    private val source: CheveretoSource,
    private val title: String? = null,
    private val description: String? = null,
    private val tags: String? = null,
    @SerialName("album_id")
    private val albumId: String? = null,
    @SerialName("category_id")
    private val categoryId: String? = null,
    private val width: Int? = null,
    private val expiration: String? = null,
    private val nsfw: Int? = null,
    private val format: String = "json",
    @SerialName("use_file_date")
    private val useFileDate: Int? = null
) : CheveretoRequest() {
    override fun path(): String = "api/1/upload"

    override fun method(): HttpMethod = HttpMethod.Post

    override fun headers(): HeadersBuilder.() -> Unit = {
        append(HttpHeaders.Accept, "application/json")
        // 对于文件上传，Content-Type 由 Ktor 自动设置
    }
    override fun bodyParameters(): Map<String, Any> {
        val params = mutableMapOf<String, Any>()

        title?.let { params["title"] = it }
        description?.let { params["description"] = it }
        tags?.let { params["tags"] = it }
        albumId?.let { params["album_id"] = it }
        categoryId?.let { params["category_id"] = it }
        width?.let { params["width"] = it }
        expiration?.let { params["expiration"] = it }
        nsfw?.let { params["nsfw"] = it }
        params["format"] = format
        useFileDate?.let { params["use_file_date"] = it }

        return params
    }

    override fun toJSON(): String = Json.encodeToString(this)

    override fun getResponse(
        responseJson: String,
        httpStatusCode: HttpStatusCode
    ): ResponseResult<CheveretoUploadResponse, FailedCheveretoResponse> {
        return try {
            if (httpStatusCode.isSuccess()) {
                val successResponse = Json.decodeFromString<CheveretoUploadResponse>(responseJson)
                ResponseResult.Success(successResponse)
            } else {
                ResponseResult.Failure(
                    FailedCheveretoResponse.Default(
                        httpStatusCode = HttpStatusCode.InternalServerError,
                        failedMessage = responseJson.takeIf { it.isNotBlank() }?:"ERROR"
                    )
                )
            }
        } catch (e: Exception) {
            ResponseResult.Failure(
                FailedCheveretoResponse.Default(
                    httpStatusCode = HttpStatusCode.InternalServerError,
                    failedMessage = "Failed to parse response: ${e.message}. Raw response: $responseJson"
                )
            )
        }
    }

    override fun expectedResponseType(): String = "CheveretoUploadResponse"

    override fun expectedFailureType(): String = "FailedCheveretoResponse"

    override fun shouldRetryOnFailure(): Boolean = true
}
