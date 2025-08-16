package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 好友信息
 */
@Serializable
data class FriendInfo(
    @SerialName("birthday_year")
    val birthdayYear: Int,
    @SerialName("birthday_month")
    val birthdayMonth: Int,
    @SerialName("birthday_day")
    val birthdayDay: Int,
    @SerialName("user_id")
    val userId: Long,
    val age: Int,
    @SerialName("phone_number")
    val phoneNum: String,
    val email: String,
    @SerialName("category_id")
    val categoryId: Int,
    val nickname: String,
    val remark: String,
    val sex: String,
    val level: Int
)