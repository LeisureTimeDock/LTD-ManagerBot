package top.r3944realms.ltdmanager

import kotlinx.coroutines.*
import top.r3944realms.ltdmanager.blessingskin.BlessingSkinClient
import top.r3944realms.ltdmanager.core.mysql.MysqlHikariConnectPool
import top.r3944realms.ltdmanager.module.ModuleManager
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.sql.Connection
import java.util.concurrent.atomic.AtomicBoolean

object GlobalManager {
    // 单例作用域，可在模块中使用协程
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val isRunning = AtomicBoolean(true)
    // Hikari 数据源
    private val dataSource: MysqlHikariConnectPool by lazy {
        MysqlHikariConnectPool()
    }

    // NapCat 客户端
    val napCatClient: NapCatClient by lazy {
        NapCatClient.create()
    }
    val blessingSkinClient: BlessingSkinClient by lazy {
        BlessingSkinClient.create()
    }

    val moduleManager: ModuleManager by lazy { ModuleManager() }

    /**
     * 获取数据库连接
     * 使用 try-with-resources 时会自动关闭
     */
    fun getConnection(): Connection {
        return dataSource.getConnection()
    }
    fun runBlockingMain(block: suspend () -> Unit) = runBlocking {
        // 注册全局关闭钩子
        LoggerUtil.addShutdownHook {
            shutdownResources()
        }
        // 启动逻辑交给外部传入
        block()

        // 注册优雅关闭
        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownGracefully()
        })

        // 保持运行
        keepRunning()
    }

    private fun keepRunning() = runBlocking {
        while (isRunning.get()) {
            delay(1000L)
        }
    }

    private fun shutdownResources() {
        val resources = listOf(
            "NapCatClient" to { napCatClient.close() },
            "BlessingSkinClient" to { blessingSkinClient.close() },
            "Hikari 数据源" to { dataSource.close() }
        )

        resources.forEach { (name, closer) ->
            try {
                LoggerUtil.syncInfo("关闭 $name")
                closer()
                LoggerUtil.syncInfo("$name 关闭完成")
            } catch (e: Exception) {
                LoggerUtil.syncError("关闭 $name 失败", e)
            }
        }
    }

    fun shutdownGracefully() = runBlocking {
        LoggerUtil.syncInfo("\n收到退出信号，正在停止所有模块...")

        moduleManager.stopAllModules()

        LoggerUtil.syncInfo("模块卸载完成，开始关闭资源...")

        // 这会触发 LoggerUtil 中注册的关闭钩子
        LoggerUtil.shutdownGracefully()

        isRunning.set(false)
    }

}