package top.r3944realms.ltdmanager.dglab

import com.r3944realms.dg_lab.api.manager.Status
import com.r3944realms.dg_lab.api.operation.ClientOperation
import com.r3944realms.dg_lab.api.operation.ServerOperation
import com.r3944realms.dg_lab.api.websocket.message.role.WebSocketClientRole
import com.r3944realms.dg_lab.api.websocket.message.role.WebSocketServerRole
import com.r3944realms.dg_lab.manager.DGPBClientManager
import com.r3944realms.dg_lab.manager.DGPBServerManager
import com.r3944realms.dg_lab.websocket.PowerBoxWSClient
import com.r3944realms.dg_lab.websocket.PowerBoxWSServer
import com.r3944realms.dg_lab.websocket.sharedData.ClientPowerBoxSharedData
import com.r3944realms.dg_lab.websocket.sharedData.ServerPowerBoxSharedData
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.dglab.manager.ClientManager
import top.r3944realms.ltdmanager.dglab.manager.ServerManager
import top.r3944realms.ltdmanager.dglab.model.game.Player
import top.r3944realms.ltdmanager.dglab.model.game.PlayerManager
import kotlin.io.path.Path

/**
 * DG_Lab管理器
 */
class DgLab {
    // 可空，延迟初始化
    internal var serverManager: ServerManager? = null
        get() = field

    internal var clientManager: ClientManager? = null
        get() = field

    private var playerManager: PlayerManager? = null
    companion object {
        const val SERVER_ROLE_NAME = "Se-IC"
    }
    fun isSeverOnline(): Boolean = serverManager?.let { it.status == Status.RUNNING } ?: false

    fun isClientOnline(id: String): Boolean = clientManager?.getClient(id)?.let { it.status == Status.RUNNING } ?: false

    fun getPlayerManager(): PlayerManager = playerManager!!

    fun close() {
        serverManager?.stop()
        clientManager?.stopAll()
    }

    fun initOrLoadPlayerManager(idNameMap: Map<Long, String>) {
        playerManager = PlayerManager(1)
        val idList = idNameMap.map { id -> id.key }
        val existingIds = playerManager?.allPlayers()?.map { it.id }?.toSet() ?: emptySet()
        val targetIds = idList.toSet()

        // 要删除的
        val toRemove = existingIds - targetIds
        // 要新增的
        val toAdd = targetIds - existingIds

        // 删除
        toRemove.forEach { id ->
            playerManager?.removePlayer(id)
        }

        // 新增
        toAdd.forEach { id ->
            playerManager?.addPlayer(Player(id, idNameMap[id] as String,false))
        }
    }

    fun createServerManager(operation: ServerOperation): DGPBServerManager {
        val loadDgLabConfig = YamlConfigLoader.loadDgLabConfig()

        val boxWSServer = PowerBoxWSServer.Builder.getBuilder()
            .port(loadDgLabConfig.wsServer.localServerPort)
            .role(WebSocketServerRole(SERVER_ROLE_NAME))
            .operation(operation)
            .sharedData(ServerPowerBoxSharedData())
            .build()
        if (loadDgLabConfig.wsServer.localServerSecure) {
            boxWSServer.enableSSL(Path(loadDgLabConfig.wsServer.localServerSslCert).toFile(), Path(loadDgLabConfig.wsServer.localServerSslKey).toFile(), loadDgLabConfig.wsServer.decryptedLocalServerSslPassword)
        }
        val dgpbServerManager = DGPBServerManager(boxWSServer)
        return dgpbServerManager
    }

    /**
     * 初始化 服务器管理类
     */
    fun initServerManager(server: DGPBServerManager) {
        serverManager = ServerManager(server)
    }
    /**
     * 初始化 客户端管理类
     */
    fun initClientManager() {
        clientManager = ClientManager()
    }

    /**
     * 添加 客户端管理类
     */
    fun addClient(key: String, client: DGPBClientManager) {
        clientManager?.addClient(key, client)
    }

    /**
     * 移除 客户端管理类
     */
    fun removeClient(key: String) {
        clientManager?.removeClient(key)
    }
    /**
     * 获取 服务器管理类
     */
    @Throws(IllegalStateException::class)
    fun getServer(): DGPBServerManager {
        return serverManager?.getInstance() ?: throw IllegalStateException("Server is not initialized")
    }
    /**
     * 获取 客户端管理类
     */
    fun getClient(key: String): DGPBClientManager? {
        return clientManager?.getClient(key)
    }
    /**
     * 获取 & 创建 客户端管理类
     */
    fun getClientOrCreate(key: String, operation: ClientOperation): DGPBClientManager {
        val client = getClient(key)
        if (client == null) {
            val loadDgLabConfig = YamlConfigLoader.loadDgLabConfig()
            val boxWSClient = PowerBoxWSClient.Builder.getBuilder()
                .address(loadDgLabConfig.wsServer.localServerUrl)
                .port(loadDgLabConfig.wsServer.localServerPort)
                .role(WebSocketClientRole("QQ-$key"))
                .operation(operation)
                .sharedData(ClientPowerBoxSharedData())
                .useRoleMsgMode(true)
                .build()

            if (loadDgLabConfig.wsServer.localServerSecure) {
                boxWSClient.enableSSL()
            }
            val clientManager = DGPBClientManager(
                boxWSClient
            )
            this.clientManager?.addClient(key, clientManager)
            return clientManager
        }
        return client
    }
}