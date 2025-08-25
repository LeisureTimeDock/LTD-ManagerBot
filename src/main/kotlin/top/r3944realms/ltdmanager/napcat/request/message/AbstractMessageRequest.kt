package top.r3944realms.ltdmanager.napcat.request.message

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractMessageRequest
    : NapCatRequest() {
    override fun header(): String {
        return "message"
    }
}