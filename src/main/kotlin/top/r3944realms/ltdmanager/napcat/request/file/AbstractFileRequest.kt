package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

@Serializable
abstract class AbstractFileRequest
    : NapCatRequest() {
    override fun header(): String {
        return "file"
    }
}