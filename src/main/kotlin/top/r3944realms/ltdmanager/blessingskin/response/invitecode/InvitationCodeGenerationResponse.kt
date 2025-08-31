package top.r3944realms.ltdmanager.blessingskin.response.invitecode

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
@Serializable
data class InvitationCodeGenerationResponse(
    val success: Boolean,
    val message: String,
    val data: List<InvitationCode>? = null
) : BlessingSkinResponse() {

    @Serializable
    data class InvitationCode(
        val code: String,
        @SerialName("generated_at")
        val generatedAt: String,
        @SerialName("expires_at")
        val expiresAt: String
    )
}