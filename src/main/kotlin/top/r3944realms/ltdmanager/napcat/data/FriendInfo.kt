package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * 好友信息
 */
@Serializable
data class FriendInfo @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("birthday_year")
    val birthdayYear: Int,
    @SerialName("birthday_month")
    val birthdayMonth: Int,
    @SerialName("birthday_day")
    val birthdayDay: Int,
    @SerialName("user_id")
    val userId: Int,
    val age: Int,
    @JsonNames("phone_number", "phone_num")
    val phoneNum: String,
    val email: String,
    @SerialName("category_id")
    val categoryId: Int,
    val nickname: String,
    val remark: String,
    val sex: SexV2,
    val level: Int
)