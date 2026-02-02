package top.r3944realms.ltdmanager.blessingskin.request

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.ResponseResult

@Serializable
abstract class BlessingSkinRequest<out T : BlessingSkinResponse, out F : FailedBlessingSkinResponse>(
    @Transient
    open val createTime: Long = System.currentTimeMillis()
) {
    /**
     * 转换为JSON字符串
     */
    abstract fun toJSON(): String

    /**
     * 获取API路径（不包含基础URL）
     * 例如: "invitation-codes/generate"
     */
    abstract fun path(): String

    /**
     * 获取HTTP方法，默认为GET（因为大多数API使用GET+查询参数）
     */
    open fun method(): HttpMethod = HttpMethod.Get

    /**
     * 自定义请求头
     */
    open fun headers(): HeadersBuilder.() -> Unit = {
        // 默认添加Content-Type
        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        // 添加Accept头
        append(HttpHeaders.Accept, "application/json")
    }

    /**
     * 获取查询参数（用于URL参数）
     * 例如: mapOf("token" to "abc123", "amount" to "1")
     */
    open fun queryParameters(): Map<String, String> = emptyMap()

    /**
     * 获取请求体参数（用于POST请求的JSON body）
     * 例如: mapOf("token" to "abc123", "amount" to 1)
     */
    open fun bodyParameters(): Map<String, Any> = emptyMap()

    /**
     * 获取请求体内容类型，默认为Application.Json
     */
    open fun contentType(): ContentType = ContentType.Application.Json

    /**
     * 解析响应JSON字符串
     * @param responseJson 响应JSON字符串
     * @param httpStatusCode HTTP状态码
     */
    abstract fun getResponse(responseJson: String, httpStatusCode: HttpStatusCode): ResponseResult<T, F>

    /**
     * 获取预期的成功响应类型名称（用于日志和调试）
     */
    abstract fun expectedResponseType(): String

    /**
     * 获取预期的失败响应类型名称（用于日志和调试）
     */
    abstract fun expectedFailureType(): String

    /**
     * 是否需要在失败时重试（默认重试）
     */
    open fun shouldRetryOnFailure(): Boolean = true
}
