package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmojiLikesList (
    @SerialName("headUrl")
    val headURL: String,

    val nickName: String,

    @SerialName("tinyId")
    val tinyID: String
)