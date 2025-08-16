package top.r3944realms.ltdmanager.napcat.requests.passkey

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractPassKeyRequest
    : NapCatRequest() {
    override fun header(): String {
        return "passkey"
    }
}