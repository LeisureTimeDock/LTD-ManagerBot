package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractGroupRequest
    : NapCatRequest() {
    override fun header(): String {
        return "group"
    }
}