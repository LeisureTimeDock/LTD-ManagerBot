package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Character (
    /**
     * 人物ID
     */
    @SerialName("character_id")
    val characterID: String,

    /**
     * 人物名字
     */
    @SerialName("character_name")
    val characterName: String,

    /**
     * 试听网址
     */
    @SerialName("preview_url")
    val previewURL: String
)