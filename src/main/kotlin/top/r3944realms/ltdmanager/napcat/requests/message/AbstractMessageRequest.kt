package top.r3944realms.ltdmanager.napcat.requests.message

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractMessageRequest
    : NapCatRequest() {
    override fun header(): String {
        return "message"
    }
}