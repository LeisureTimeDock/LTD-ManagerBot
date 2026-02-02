package top.r3944realms.ltdmanager.blessingskin.response.invitecode

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.blessingskin.data.InvitationCode
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
@Serializable
data class InvitationCodeGenerationResponse(
    val success: Boolean,
    val message: String,
    val data: List<InvitationCode>? = null
) : BlessingSkinResponse() {

}