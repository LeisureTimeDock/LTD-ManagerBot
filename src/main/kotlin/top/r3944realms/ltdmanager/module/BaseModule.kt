package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.utils.LoggerUtil
import kotlin.coroutines.cancellation.CancellationException

/**
 * 模块抽象基类
 * 所有功能模块都继承该类
 */
abstract class BaseModule(baseName : String = "BaseModule", idName : String = "")  {

    /**
     * 模块名称
     */
    val name: String = "$baseName-#$idName";

    /**
     * 停止信号
     */
    private val stopSignal = CompletableDeferred<Unit>()
    /**
     * 模块是否加载
     */
    @Volatile
    var loaded: Boolean = false
        private set

    /**
     * 模块加载
     * 可以在这里初始化协程、监听器、定时任务等
     */
    open fun load() {
        if (!loaded) {
            loaded = true
            onLoad()
            LoggerUtil.logger.info("模块加载: $name")
        }
    }

    /**
     * 模块卸载
     * 清理资源，取消协程、关闭监听器等
     */
    open suspend fun unload() {
        if (loaded) {
            loaded = false
            onUnload()
            stopSignal.complete(Unit)
            LoggerUtil.logger.info("模块卸载: $name")
        }
    }

    /**
     * 模块加载时的实际逻辑，由子类实现
     */
    protected abstract fun onLoad()

    /**
     * 模块卸载时的实际逻辑，由子类实现
     */
    protected abstract suspend fun onUnload()
    /**
     * 可选的停止方法，模块内部协程等后台任务在这里被取消
     */
    open suspend fun stop() {
        if (!loaded) return
        LoggerUtil.syncInfo("[$name] 收到停止命令")
        unload() // 默认实现直接卸载
        try {
            stopSignal.await()
        } catch (_: CancellationException) {}
        LoggerUtil.syncInfo("[$name] 模块已安全停止")
    }
    /**
     * 模块说明 / 帮助信息
     * 默认返回空字符串，子类可重写提供具体帮助文本
     */
    open fun help(): String = ""
    /** 模块基础信息，用于 HelpModule 显示 */
    open fun info(): String =  "模块 $name 未提供详细信息"
    /**
     * 提供访问全局 NapCatClient 的快捷方式
     */
    protected val napCatClient get() = GlobalManager.napCatClient

    /**
     * 提供访问全局 blessingSkinClient 的快捷方式
     */
    protected val blessingSkinClient get() = GlobalManager.blessingSkinClient

    /**
     * 提供访问全局 mcSrvStatusClient 的快捷方式
     */
    protected val mcSrvStatusClient get() = GlobalManager.mcSrvStatusClient

    /**
     * 提供访问全局 加载模块 的快捷方式
     */
    protected val moduleMap get() = GlobalManager.moduleManager.getModules()

    /**
     * 获取数据库连接
     * 使用 try-with-resources 时会自动关闭
     */
    protected fun getConnection() = GlobalManager.getConnection()
    /**
     * 安全获取 NapCatClient，避免空指针异常
     */
    protected fun getNapCatClientOrNull() = try {
        GlobalManager.napCatClient
    } catch (e: Exception) {
        LoggerUtil.logger.warn("获取NapCatClient失败", e)
        null
    }

    /**
     * 安全获取 NapCatClient，如果获取失败则抛出详细异常
     */
    protected fun getNapCatClientOrThrow(): Any {
        val client = try {
            GlobalManager.napCatClient
        } catch (e: Exception) {
            throw IllegalStateException("无法获取NapCatClient，请检查GlobalManager初始化状态", e)
        }
        return client ?: throw IllegalStateException("NapCatClient为null，请检查GlobalManager初始化")
    }
}