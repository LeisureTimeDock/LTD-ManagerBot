
package top.r3944realms.ltdmanager.napcat.event.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetUnidirectionalFriendList事件
 * @property data 响应数据
 */
@Developing
@Serializable
data class GetUnidirectionalFriendListEvent(
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

    val data: List<Datum>
) : AbstractAccountEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        val age: Double,

        @SerialName("nick_name")
        val nickName: String,

        val source: String,
        val uid: String,
        val uin: Double
    )
    override fun subtype(): String {
        return "get_unidirectional_friend_list"
    }
}
