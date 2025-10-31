package top.r3944realms.ltdmanager.dglab.model.game

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.module.PersistentState
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class PlayerManager(id: Long): PersistentState<PlayerManager.PlayerState> {
    @Contextual
    private val map = ConcurrentHashMap<Long, Player>()
    @Transient
    private val stateFile: File = getStateFileInternal("dglab_player_data.json", "dglab$id")
    @Transient
    private val stateBackupFile: File = getStateFileInternal("dglab_player_data.json.bak","dglab$id")
    override fun getStateFileInternal(): File = stateFile

    private var playerState = loadState()
    @Serializable
    data class PlayerState(
        val map: Map<Long, Player> = emptyMap()
    )

    override fun getState(): PlayerState = playerState
    /** 添加或更新玩家 */
    fun addPlayer(player: Player) {
        map[player.id] = player
    }

    /** 根据 ID 获取玩家 */
    fun getPlayer(id: Long): Player? = map[id]

    /** 删除玩家 */
    fun removePlayer(id: Long): Player? = map.remove(id)

    /** 判断是否存在玩家 */
    fun contains(id: Long): Boolean = map.containsKey(id)

    /** 获取所有玩家 */
    fun allPlayers(): List<Player> = map.values.toList()

    /** 获取所有在线玩家的数量 */
    fun getOnlinePlayerSize(): Int = map.values.filter { it.active }.size


    override fun saveState(state: PlayerState) {
        try {
            if (stateFile.exists()) stateFile.copyTo(stateBackupFile, overwrite = true)
            stateFile.writeText(Json.encodeToString(state))
        } catch (e: Exception) {
            LoggerUtil.logger.error("[dglab] 保存玩家数据&状态失败", e)
        }
    }

    override fun loadState(): PlayerState {
        return try {
            val fileToRead = when {
                stateFile.exists() -> stateFile
                stateBackupFile.exists() -> stateBackupFile
                else -> null
            } ?: return PlayerState()

            Json.decodeFromString<PlayerState>(fileToRead.readText())
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[dglab] 读取玩家数据&状态失败", e)
            PlayerState()
        }
    }
}