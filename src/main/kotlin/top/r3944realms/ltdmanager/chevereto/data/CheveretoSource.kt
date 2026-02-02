package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.Serializable

@Serializable
sealed class CheveretoSource {
    @Serializable
    data class ByteArraySource(val bytes: ByteArray, val fileName: String) : CheveretoSource()
    @Serializable
    data class UrlSource(val url: String) : CheveretoSource()
}