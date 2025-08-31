package top.r3944realms.ltdmanager.blessingskin.request.invitecode

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.blessingskin.request.BlessingSkinRequest
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.ResponseResult
import top.r3944realms.ltdmanager.blessingskin.response.invitecode.InvitationCodeGenerationResponse
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import java.util.*

@Serializable
class GenerateInvitationCodeRequest(
    @Transient
    val token: String? = null,
    @Transient
    val amount: Int? = 1,
    @Transient
    override val createTime: Long = System.currentTimeMillis()
) : BlessingSkinRequest<InvitationCodeGenerationResponse, FailedBlessingSkinResponse.Default>() {

    override fun toJSON(): String {
        // 对于GET请求，参数在URL中，body可以为空
        return "{}"
    }

    override fun path(): String {
        return YamlConfigLoader.loadBlessingSkinServerConfig().invitationApi?.path ?: "api/invitation-codes/generate"
    }

    override fun method(): HttpMethod {
        return HttpMethod.Post // 使用POST方法，参数在查询JSON中
    }

    override fun queryParameters(): Map<String, String> {
        val params = mutableMapOf<String, String>()

        // 添加token参数（如果提供）
        token?.let { params["token"] = it }

        // 添加amount参数（如果提供）
        amount?.let { params["amount"] = it.toString() }

        return params
    }

    override fun headers(): HeadersBuilder.() -> Unit = {
        // 调用父类的默认headers
        super.headers().invoke(this)
        // 可以添加额外的header
        append("X-Request-ID", UUID.randomUUID().toString())
    }

    override fun getResponse(
        responseJson: String,
        httpStatusCode: HttpStatusCode
    ): ResponseResult<InvitationCodeGenerationResponse, FailedBlessingSkinResponse.Default> {
        return try {
            // 使用BlessingSkinResponse的伴生对象方法解析
            val response = BlessingSkinResponse.decode(responseJson) as? InvitationCodeGenerationResponse
                ?: throw IllegalArgumentException("响应类型不匹配")

            ResponseResult.Success(response)
        } catch (e: Exception) {
            ResponseResult.Failure(
                FailedBlessingSkinResponse.Default(
                    failedResult = "解析响应失败: ${e.message}"
                )
            )
        }
    }

    override fun expectedResponseType(): String {
        return "invitation_code_generation"
    }

    override fun expectedFailureType(): String {
        return "default_failure"
    }

    override fun shouldRetryOnFailure(): Boolean {
        return false
    }
}