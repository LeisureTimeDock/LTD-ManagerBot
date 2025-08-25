package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractAccountRequest
    : NapCatRequest() {
    override fun header(): String {
        return "account"
    }
}