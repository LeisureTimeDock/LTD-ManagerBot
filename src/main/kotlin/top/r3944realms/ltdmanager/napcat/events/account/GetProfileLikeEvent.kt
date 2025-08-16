
package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetProfileLike事件
 * @property data 响应数据
 */
@Serializable
data class GetProfileLikeEvent(
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

    val data: Data
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        /**
         * 互赞信息
         */
        val favoriteInfo: FavoriteInfo,

        val time: Double,
        val uid: String,

        /**
         * 点赞信息
         */
        val voteInfo: VoteInfo
    )

    /**
     * 互赞信息
     */
    @Serializable
    data class FavoriteInfo (
        /**
         * 最后点赞时间（不是时间戳）
         */
        @SerialName("last_time")
        val lastTime: Double,

        /**
         * 上次次数
         */
        @SerialName("today_count")
        val todayCount: Double,

        /**
         * 总次数
         */
        @SerialName("total_count")
        val totalCount: Double,

        val userInfos: List<LikeInfo>
    )

    /**
     * 点赞信息
     */
    @Serializable
    data class LikeInfo (
        val age: Double,
        val bAvailableCnt: Double,
        val bTodayVotedCnt: Double,
        val count: Double,

        @SerialName("customId")
        val customID: Double,

        val gender: Double,
        val giftCount: Double,
        val isFriend: Boolean,
        val isSvip: Boolean,
        val isvip: Boolean,
        val lastCharged: Double,
        val latestTime: Double,
        val nick: String,
        val src: Double,
        val uid: String,
        val uin: Double
    )

    /**
     * 点赞信息
     */
    @Serializable
    data class VoteInfo (
        @SerialName("last_visit_time")
        val lastVisitTime: Double,

        /**
         * 点赞次数
         */
        @SerialName("new_count")
        val newCount: Double,

        @SerialName("new_nearby_count")
        val newNearbyCount: Double,

        /**
         * 总次数
         */
        @SerialName("total_count")
        val totalCount: Double,

        val userInfos: List<LikeInfo>
    )
    override fun subtype(): String {
        return "get_profile_like"
    }
}
