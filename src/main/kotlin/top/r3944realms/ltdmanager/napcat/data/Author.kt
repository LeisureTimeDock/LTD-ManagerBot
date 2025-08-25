package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author (
    @SerialName("groupId")
    val groupId: String,

    val groupName: String,

    @SerialName("numId")
    val numId: String,

    @SerialName("strId")
    val strId: String,

    val type: Double,
    val uid: String
)