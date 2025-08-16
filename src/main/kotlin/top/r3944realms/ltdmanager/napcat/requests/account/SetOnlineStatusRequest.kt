package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SetOnlineStatusRequest(
    /**
     * 电量
     */
    val batteryStatus: Double,

    /**
     * 详情看顶部
     */
    val extStatus: Double,

    /**
     * 详情看顶部
     */
    val status: Double
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_online_status"
    companion object {
        // Basic statuses
        val ONLINE = SetOnlineStatusRequest(0.0, 0.0, 10.0)
        val Q_ME = SetOnlineStatusRequest(0.0, 0.0, 60.0)
        val AWAY = SetOnlineStatusRequest(0.0, 0.0, 30.0)
        val BUSY = SetOnlineStatusRequest(0.0, 0.0, 50.0)
        val DO_NOT_DISTURB = SetOnlineStatusRequest(0.0, 0.0, 70.0)
        val INVISIBLE = SetOnlineStatusRequest(0.0, 0.0, 40.0)

        // Extended statuses (all with status = 10.0)
        val LISTENING_TO_MUSIC = SetOnlineStatusRequest(0.0, 1028.0, 10.0)
        val SPRING_LIMITED = SetOnlineStatusRequest(0.0, 2037.0, 10.0)
        val PLAYING_YUANMENG = SetOnlineStatusRequest(0.0, 2025.0, 10.0)
        val LOOKING_FOR_STAR_PARTNER = SetOnlineStatusRequest(0.0, 2026.0, 10.0)
        val DRAINED = SetOnlineStatusRequest(0.0, 2014.0, 10.0)
        val TODAYS_WEATHER = SetOnlineStatusRequest(0.0, 1030.0, 10.0)
        val I_CRASHED = SetOnlineStatusRequest(0.0, 2019.0, 10.0)
        val LOVE_YOU = SetOnlineStatusRequest(0.0, 2006.0, 10.0)
        val IN_LOVE = SetOnlineStatusRequest(0.0, 1051.0, 10.0)
        val GOOD_LUCK_KOI = SetOnlineStatusRequest(0.0, 1071.0, 10.0)
        val MERCURY_RETROGRADE = SetOnlineStatusRequest(0.0, 1201.0, 10.0)
        val HAVING_FUN = SetOnlineStatusRequest(0.0, 1056.0, 10.0)
        val FULL_OF_ENERGY = SetOnlineStatusRequest(0.0, 1058.0, 10.0)
        val BABY_CERTIFIED = SetOnlineStatusRequest(0.0, 1070.0, 10.0)
        val COMPLICATED = SetOnlineStatusRequest(0.0, 1063.0, 10.0)
        val RARE_CONFUSION = SetOnlineStatusRequest(0.0, 2001.0, 10.0)
        val EMO = SetOnlineStatusRequest(0.0, 1401.0, 10.0)
        val LIFE_IS_HARD = SetOnlineStatusRequest(0.0, 1062.0, 10.0)
        val I_UNDERSTAND = SetOnlineStatusRequest(0.0, 2013.0, 10.0)
        val IM_OKAY = SetOnlineStatusRequest(0.0, 1052.0, 10.0)
        val WANT_SILENCE = SetOnlineStatusRequest(0.0, 1061.0, 10.0)
        val LEISURELY = SetOnlineStatusRequest(0.0, 1059.0, 10.0)
        val TRAVELING = SetOnlineStatusRequest(0.0, 2015.0, 10.0)
        val WEAK_SIGNAL = SetOnlineStatusRequest(0.0, 1011.0, 10.0)
        val GOING_OUT = SetOnlineStatusRequest(0.0, 2003.0, 10.0)
        val DOING_HOMEWORK = SetOnlineStatusRequest(0.0, 2012.0, 10.0)
        val STUDYING = SetOnlineStatusRequest(0.0, 1018.0, 10.0)
        val WORKING_HARD = SetOnlineStatusRequest(0.0, 2023.0, 10.0)
        val SLACKING_OFF = SetOnlineStatusRequest(0.0, 1300.0, 10.0)
        val BORED = SetOnlineStatusRequest(0.0, 1060.0, 10.0)
        val PLAYING_GAMES = SetOnlineStatusRequest(0.0, 1027.0, 10.0)
        val SLEEPING = SetOnlineStatusRequest(0.0, 1016.0, 10.0)
        val STAYING_UP_LATE = SetOnlineStatusRequest(0.0, 1032.0, 10.0)
        val WATCHING_DRAMA = SetOnlineStatusRequest(0.0, 1021.0, 10.0)
        val BATTERY_STATUS = SetOnlineStatusRequest(0.0, 1000.0, 10.0)

        // Helper function to create custom battery status
        fun withBatteryLevel(batteryLevel: Double, baseStatus: SetOnlineStatusRequest): SetOnlineStatusRequest {
            return baseStatus.copy(batteryStatus = batteryLevel)
        }
    }
}