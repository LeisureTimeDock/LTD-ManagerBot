package top.r3944realms.ltdmanager.dglab.manager

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
import kotlin.io.path.Path

/**
 * 全局DG_Lab单例管理器
 */
object DgLabManager {
    // 可空，延迟初始化
    var serverManager: ServerManager? = null
        private set

    var clientManager: ClientManager? = null
        private set


    fun createServerManager(operation: ServerOperation): DGPBServerManager {
        val loadDgLabConfig = YamlConfigLoader.loadDgLabConfig()
        val boxWSServer = PowerBoxWSServer.Builder.getBuilder()
            .port(loadDgLabConfig.wsServer.localServerPort)
            .role(WebSocketServerRole("Se-IC"))
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