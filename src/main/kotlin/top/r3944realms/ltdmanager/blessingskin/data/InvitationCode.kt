package top.r3944realms.ltdmanager.blessingskin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationCode(
    val code: String,
    @SerialName("generated_at")
    val generatedAt: String,
    @SerialName("expires_at")
    val expiresAt: String
)