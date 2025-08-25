package top.r3944realms.ltdmanager.module

import top.r3944realms.ltdmanager.GlobalManager

/**
 * 模块抽象基类
 * 所有功能模块都继承该类
 */
abstract class BaseModule {

    /**
     * 模块名称
     */
    abstract val name: String

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
        }
    }

    /**
     * 模块卸载
     * 清理资源，取消协程、关闭监听器等
     */
    open fun unload() {
        if (loaded) {
            loaded = false
            onUnload()
        }
    }

    /**
     * 模块加载时的实际逻辑，由子类实现
     */
    protected abstract fun onLoad()

    /**
     * 模块卸载时的实际逻辑，由子类实现
     */
    protected abstract fun onUnload()
    /**
     * 可选的停止方法，模块内部协程等后台任务在这里被取消
     */
    open suspend fun stop() {
        unload() // 默认实现直接卸载
    }
    /**
     * 提供访问全局 NapCatClient 的快捷方式
     */
    protected val napCatClient get() = GlobalManager.napCatClient

    /**
     * 获取数据库连接
     * 使用 try-with-resources 时会自动关闭
     */
    protected fun getConnection() = GlobalManager.getConnection()
}