package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author (
    @SerialName("groupId")
    val groupID: String,

    val groupName: String,

    @SerialName("numId")
    val numID: String,

    @SerialName("strId")
    val strID: String,

    val type: Double,
    val uid: String
)