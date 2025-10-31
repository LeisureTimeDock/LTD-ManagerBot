package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheveretoResponse(
    @SerialName("status_code")
    val statusCode: Int,
    val success: Success? = null,
    val image: CheveretoImage? = null,
    @SerialName("status_txt")
    val statusTxt:String ?= null
)