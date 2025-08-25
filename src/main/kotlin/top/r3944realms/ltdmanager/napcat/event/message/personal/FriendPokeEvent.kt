
package top.r3944realms.ltdmanager.napcat.event.message.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.event.group.AbstractGroupEvent

/**
 * FriendPoke事件
 */
@Serializable
data class FriendPokeEvent(
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
) : AbstractGroupEvent(status0, retcode0, message0, wording0, echo0) {
    
    override fun subtype(): String {
        return "friend_poke"
    }
}
