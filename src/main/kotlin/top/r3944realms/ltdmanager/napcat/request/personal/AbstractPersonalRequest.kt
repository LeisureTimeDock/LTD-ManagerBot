package top.r3944realms.ltdmanager.napcat.request.personal

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractPersonalRequest
    : NapCatRequest() {
    override fun header(): String {
        return "personal"
    }
}