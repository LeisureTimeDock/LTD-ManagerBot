package top.r3944realms.ltdmanager.napcat.requests.system

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractSystemRequest
    : NapCatRequest() {
    override fun header(): String {
        return "system"
    }
}