package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupMember(
    @SerialName("group_id")
    val groupId: Long,
    @SerialName("user_id")
    val userId: Long,
    val nickname: String,
    val card: String,
    val sex: String,
    val age: Int,
    val area: String,
    val level: String,
    @SerialName("qq_level")
    val qqLevel: Int,
    @SerialName("join_time")
    val joinTime: Long,
    @SerialName("last_sent_time")
    val lastSentTime: Long,
    @SerialName("title_expire_time")
    val titleExpireTime: Long,
    val unfriendly: Boolean,
    @SerialName("card_changeable")
    val cardChangeable: Boolean,
    @SerialName("is_robot")
    val isRobot: Boolean,
    @SerialName("shut_up_timestamp")
    val shutUpTimestamp: Long,
    val role: String,
    val title: String
)