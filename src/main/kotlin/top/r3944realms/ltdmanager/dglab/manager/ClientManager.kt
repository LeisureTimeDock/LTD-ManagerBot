package top.r3944realms.ltdmanager.dglab.manager

import com.r3944realms.dg_lab.manager.DGPBClientManager

class ClientManager(
    private val clients: MutableMap<String, DGPBClientManager> = mutableMapOf(),
) : IManager<MutableMap<String, DGPBClientManager>> {

    /**
     * 添加单例客户端管理示例
     * @param key 唯一标识客户端管理的 key，比如 ID 或 name
     */
    fun addClient(key: String, client: DGPBClientManager) {
        clients[key] = client
    }

    /**
     * 移除单例客户端管理实例
     */
    fun removeClient(key: String) {
        clients.remove(key)?.stop()
    }

    /**
     * 根据 key 获取客户端
     */
    fun getClient(key: String): DGPBClientManager? {
        return clients[key]
    }

    /**
     * 启动所有客户端
     */
    override fun startAll() {
        clients.values.forEach { it.start() }
    }

    /**
     * 停止所有客户端
     */
    override fun stopAll() {
        clients.values.forEach { it.stop() }
    }

    /**
     * 获取内部 Map 实例
     */
    override fun getInstance(): MutableMap<String, DGPBClientManager> {
        return clients
    }
}
