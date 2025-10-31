package top.r3944realms.ltdmanager.napcat.data.msghistory

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.data.ID

@Serializable
data class MsgHistoryMessageData (
    val text: String? = null,
    val name: String? = null,
    val qq: ID? = null,
    val id: ID? = null,
    val file: String? = null,

    /**
     * 外显
     */
    val summary: String? = null,

    val data: String? = null,
    val content: MsgHistoryContent? = null
)