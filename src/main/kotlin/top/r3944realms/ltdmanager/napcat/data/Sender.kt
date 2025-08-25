package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * sender
 */
@Serializable
data class Sender (
    val age: Double? = null,
    val card: String,
    val nickname: String,
    val role: Role? = null,
    val sex: SexV2? = null,

    @SerialName("user_id")
    val userId: Long
)