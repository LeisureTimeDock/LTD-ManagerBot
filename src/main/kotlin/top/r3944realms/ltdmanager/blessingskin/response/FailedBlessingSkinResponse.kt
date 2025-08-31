package top.r3944realms.ltdmanager.blessingskin.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class FailedBlessingSkinResponse: BlessingSkinResponse() {
    abstract fun failedMessage(): String
    @Serializable
    class Default(@Transient val failedResult: String? = "未知错误") : FailedBlessingSkinResponse() {
        override fun failedMessage(): String = failedResult!!

    }
}