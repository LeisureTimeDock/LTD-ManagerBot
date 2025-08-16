package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MessageType(val value: String) {
    @SerialName("at") At("at"),
    @SerialName("face") Face("face"),
    @SerialName("file") File("file"),
    @SerialName("forward") Forward("forward"),
    @SerialName("image") Image("image"),
    @SerialName("json") JSON("json"),
    @SerialName("record") Record("record"),
    @SerialName("reply") Reply("reply"),
    @SerialName("text") Text("text"),
    @SerialName("video") Video("video"),
    @SerialName("music") Music("music"),
    @SerialName("dice") Dice("dice"),
    @SerialName("rps") Rps("rps"),
    @SerialName("node") Node("node");
}