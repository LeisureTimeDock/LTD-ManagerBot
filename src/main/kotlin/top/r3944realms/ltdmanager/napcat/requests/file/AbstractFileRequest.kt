package top.r3944realms.ltdmanager.napcat.requests.file

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest

@Serializable
abstract class AbstractFileRequest
    : NapCatRequest() {
    override fun header(): String {
        return "file"
    }
}