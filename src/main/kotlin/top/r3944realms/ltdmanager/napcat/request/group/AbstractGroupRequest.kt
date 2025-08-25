package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractGroupRequest
    : NapCatRequest() {
    override fun header(): String {
        return "group"
    }
}