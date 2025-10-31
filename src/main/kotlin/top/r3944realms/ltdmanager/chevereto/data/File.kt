package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.Serializable

@Serializable
data class File(
    val resource: Resource
) {
    @Serializable
    data class Resource(
        val type: String
    )
}