package top.r3944realms.ltdmanager.chevereto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheveretoResponse(
    @SerialName("status_code")
    val statusCode: Int,
    val success: Map<String, String>? = null,
    val image: CheveretoImage? = null
)