package top.r3944realms.ltdmanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import top.r3944realms.ltdmanager.core.mysql.MysqlHikariConnectPool
import top.r3944realms.ltdmanager.module.ModuleManager
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.sql.Connection

object GlobalManager {
    // 单例作用域，可在模块中使用协程
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Hikari 数据源
    private val dataSource: MysqlHikariConnectPool by lazy {
        MysqlHikariConnectPool()
    }

    // NapCat 客户端
    val napCatClient: NapCatClient by lazy {
        NapCatClient.create()
    }

    val moduleManager: ModuleManager by lazy { ModuleManager() }

    /**
     * 获取数据库连接
     * 使用 try-with-resources 时会自动关闭
     */
    fun getConnection(): Connection {
        return dataSource.getConnection()
    }

    /**
     * 关闭全局资源
     * 例如在应用退出时调用
     */
    fun shutdown() {
        try {
            LoggerUtil.logger.info("关闭 NapCatClient")
            napCatClient.close()
        } catch (e: Exception) {
            LoggerUtil.logger.warn("关闭 NapCatClient 失败", e)
        }

        try {
            LoggerUtil.logger.info("关闭 Hikari 数据源")
            dataSource.close()
        } catch (e: Exception) {
            LoggerUtil.logger.warn("关闭 Hikari 数据源失败", e)
        }
    }
}