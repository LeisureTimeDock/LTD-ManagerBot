package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.data.FriendInfo

@Serializable
data class GetFriendsWithCategoryEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,
    val data: Datum

) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    override fun subtype(): String = "get_friends_with_category"
    @Serializable
    data class Datum (
        /**
         * 好友列表
         */
        val buddyList: List<FriendInfo>,

        /**
         * 分组ID
         */
        @SerialName("categoryId")
        val categoryID: Double,

        /**
         * 好友数量
         */
        @SerialName("categoryMbCount")
        val categoryMBCount: Double,

        /**
         * 分组名
         */
        val categoryName: String,

        /**
         * 分组排序ID
         */
        @SerialName("categorySortId")
        val categorySortID: Double,

        /**
         * 在线好友数量
         */
        val onlineCount: Double
    )

}