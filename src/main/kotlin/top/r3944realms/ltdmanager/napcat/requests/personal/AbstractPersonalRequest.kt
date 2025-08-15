package top.r3944realms.ltdmanager.napcat.requests.personal

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractPersonalRequest
    : NapCatRequest() {
    override fun header(): String {
        return "personal"
    }
}