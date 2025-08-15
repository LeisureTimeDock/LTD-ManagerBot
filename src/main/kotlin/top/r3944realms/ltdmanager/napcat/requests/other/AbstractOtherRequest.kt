package top.r3944realms.ltdmanager.napcat.requests.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractOtherRequest
    : NapCatRequest() {
    override fun header(): String {
        return "other"
    }
}