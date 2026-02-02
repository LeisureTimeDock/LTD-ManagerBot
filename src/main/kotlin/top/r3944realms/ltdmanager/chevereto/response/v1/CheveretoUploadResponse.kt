package top.r3944realms.ltdmanager.chevereto.response.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.chevereto.data.CheveretoImage
import top.r3944realms.ltdmanager.chevereto.data.SuccessInfo
import top.r3944realms.ltdmanager.chevereto.response.CheveretoResponse

@Serializable
data class CheveretoUploadResponse(
    @SerialName("status_code")
    val statusCode: Int,
    val success: SuccessInfo,
    val image: CheveretoImage,
    @SerialName("status_txt")
    val statusTxt: String
) : CheveretoResponse()