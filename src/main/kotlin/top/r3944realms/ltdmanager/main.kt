package top.r3944realms.ltdmanager

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import top.r3944realms.ltdmanager.module.GroupRequestHandlerModule
import top.r3944realms.ltdmanager.module.RconPlayerListModule
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.util.concurrent.atomic.AtomicBoolean


fun main() = runBlocking {
    // 标记程序是否运行
    val isRunning = AtomicBoolean(true)

    // 创建模块实例
    val groupModule = GroupRequestHandlerModule(
        client = GlobalManager.napCatClient,
        targetGroupId = 538751386
    )
    val rconModule = RconPlayerListModule(
        pollIntervalMillis = 3_000L,
        timeout = 2_000L,
        cooldownMillis = 10_000L,
        targetGroupId = 538751386,
        selfId = 3327379836,
        selfNickName = "闲趣老土豆",
        keywords = setOf(
            //形容
            "土豆", "马铃薯", "Potato", "potato", "POTATO",
            "Potatoes", "potatoes", "POTATOES", "🥔",
            //正经
            "列表","服务器状态", "TPS", "tps", "list", "List"
        )
    );

    // 注册模块到全局模块管理器
    GlobalManager.moduleManager.registerModule(groupModule)
    GlobalManager.moduleManager.registerModule(rconModule)

    // 加载模块
    GlobalManager.moduleManager.loadModule(groupModule.name)
    GlobalManager.moduleManager.loadModule(rconModule.name)


    // 捕获 JVM 关闭信号，优雅退出
    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            LoggerUtil.logger.info("\n收到退出信号，正在停止所有模块...")
            GlobalManager.moduleManager.stopAllModules() // 批量 stop
            LoggerUtil.logger.info("模块卸载完成，程序退出。")
            GlobalManager.shutdown()
        }
        isRunning.set(false)
    })

    // 持续挂起，保持主线程运行
    while (isRunning.get()) {
        delay(1000L)
    }
}