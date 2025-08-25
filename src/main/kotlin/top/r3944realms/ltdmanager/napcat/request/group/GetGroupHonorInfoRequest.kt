
package top.r3944realms.ltdmanager.napcat.request.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * GetGroupHonorInfo请求
 */
@Serializable
data class GetGroupHonorInfoRequest(
    @SerialName("group_id")
    val groupId: ID,

    /**
     * 看详情
     */
    val type: Type? = Type.ALL
) : AbstractGroupRequest() {
    @Serializable
    enum class Type(val type: String) {
        /**
         * 所有（默认）
         */
        @SerialName("all")ALL("all"),

        /**
         * /群聊之火
         */
        @SerialName("talkactive")TALK_ACTIVE("talk_active"),

        /**
         * 	群聊炽焰
         */
        @SerialName("performer")PERFORMER("performer"),
        /**
         * 龙王
         */
        @SerialName("legend")LEGEND("legend"),
        /**
         * 冒尖小春笋（R.I.P）
         */
        @SerialName("strong_newbie")STRONG_NEWBIE("strong_newbie"),
        /**
         * 	快乐源泉
         */
        @SerialName("emotion")EMOTION("emotion")
    }
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_group_honor_info"
}
