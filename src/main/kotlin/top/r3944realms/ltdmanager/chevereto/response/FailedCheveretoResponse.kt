package top.r3944realms.ltdmanager.chevereto.response

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse

@Serializable
abstract class FailedCheveretoResponse: CheveretoResponse(), IFailedResponse {
    @Serializable
    class Default(@Transient override val httpStatusCode: HttpStatusCode = HttpStatusCode.OK, @Transient override val failedMessage: String = "未知错误") : FailedCheveretoResponse()
}