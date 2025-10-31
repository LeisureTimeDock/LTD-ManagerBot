package top.r3944realms.ltdmanager.dglab.model.game

import kotlinx.serialization.Serializable

/**
 * 玩家类
 */
@Serializable
data class Player(
    val id: Long,
    var name: String,
    var active: Boolean,
)