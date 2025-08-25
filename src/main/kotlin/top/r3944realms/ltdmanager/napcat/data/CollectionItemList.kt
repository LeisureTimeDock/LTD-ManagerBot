package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionItemList (
    val author: Author,
    val bid: Long,
    val category: Long,
    val cid: String,
    val collectTime: String,
    val createTime: String,

    @SerialName("customGroupId")
    val customGroupId: Double,

    val modifyTime: String,
    val securityBeat: Boolean,
    val sequence: String,

    @SerialName("shareUrl")
    val shareURL: String,

    val status: Double,
    val summary: Summary,
    val type: Double
)