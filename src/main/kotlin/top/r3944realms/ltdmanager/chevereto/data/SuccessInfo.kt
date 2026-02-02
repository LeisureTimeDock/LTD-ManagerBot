package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.Serializable

@Serializable
data class Success(
    val message : String? = null,
    val code: Int? = 200,
)
