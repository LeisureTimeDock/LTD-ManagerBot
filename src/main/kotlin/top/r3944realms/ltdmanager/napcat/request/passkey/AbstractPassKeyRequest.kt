package top.r3944realms.ltdmanager.napcat.request.passkey

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractPassKeyRequest
    : NapCatRequest() {
    override fun header(): String {
        return "passkey"
    }
}