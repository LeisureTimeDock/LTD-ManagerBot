package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.Serializable

@Serializable
data class ImageFile(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String,
    val size: Long
)