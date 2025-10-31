package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.Serializable

@Serializable
data class Medium(
    val filename: String? = null,
    val name: String? = null,
    val mime: String? = null,
    val extension: String? = null,
    val url: String? = null
)