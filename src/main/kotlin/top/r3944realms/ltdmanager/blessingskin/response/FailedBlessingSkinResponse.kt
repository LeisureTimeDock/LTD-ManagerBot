package top.r3944realms.ltdmanager.blessingskin.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse

@Serializable
abstract class FailedBlessingSkinResponse: BlessingSkinResponse(), IFailedResponse {
    @Serializable
    class Default(@Transient override val failedMessage: String = "未知错误") : FailedBlessingSkinResponse() {

    }
}