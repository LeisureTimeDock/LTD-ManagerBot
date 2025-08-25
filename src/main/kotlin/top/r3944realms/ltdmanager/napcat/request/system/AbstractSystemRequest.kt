package top.r3944realms.ltdmanager.napcat.request.system

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractSystemRequest
    : NapCatRequest() {
    override fun header(): String {
        return "system"
    }
}