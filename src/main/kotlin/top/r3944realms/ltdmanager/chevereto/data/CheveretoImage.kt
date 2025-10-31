package top.r3944realms.ltdmanager.chevereto

import kotlinx.serialization.Serializable

@Serializable
data class CheveretoImage(
    val name: String,
    val extension: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val date: String,
    val url: String
)