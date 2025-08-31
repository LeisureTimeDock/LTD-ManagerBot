package top.r3944realms.ltdmanager.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configurator
import java.util.concurrent.atomic.AtomicBoolean

object LoggerUtil {
    private val isShuttingDown = AtomicBoolean(false)
    private val shutdownHooks = mutableListOf<() -> Unit>()

    val logger: Logger by lazy {
        LogManager.getLogger("LTDManagerBot")
    }

    init {
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(Thread {
            shutdownGracefully()
        })
    }

    /**
     * 注册自定义关闭钩子
     */
    fun addShutdownHook(hook: () -> Unit) {
        shutdownHooks.add(hook)
    }

    /**
     * 优雅关闭日志系统
     */
    fun shutdownGracefully() {
        if (isShuttingDown.getAndSet(true)) {
            return // 避免重复关闭
        }

        try {
            // 输出关闭开始信息
            emergencyInfo("🚀 开始优雅关闭日志系统...")

            // 先执行自定义关闭钩子（业务资源关闭）
            runCustomShutdownHooks()

            // 刷新所有日志输出（确保日志文件保存）
            flushAllLogs()

            // 给日志一些时间写入磁盘
            Thread.sleep(200)

            // 关闭 Log4j2 上下文
            shutdownLog4j2()

            // 最终确认
            println("🎉 日志系统关闭完成")
        } catch (e: Exception) {
            System.err.println("❌ 关闭过程中发生错误: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * 执行自定义关闭钩子
     */
    private fun runCustomShutdownHooks() {
        if (shutdownHooks.isNotEmpty()) {
            emergencyInfo("执行 ${shutdownHooks.size} 个自定义关闭钩子")
            shutdownHooks.forEachIndexed { index, hook ->
                try {
                    emergencyInfo("执行关闭钩子 ${index + 1}")
                    hook()
                } catch (e: Exception) {
                    System.err.println("❌ 关闭钩子 ${index + 1} 执行失败: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 刷新所有日志输出 - 针对 RollingFile Appender 优化
     */
    fun flushAllLogs() {
        try {
            emergencyInfo("正在刷新日志输出...")

            val context = LogManager.getContext(false)
            if (context is LoggerContext) {
                // 获取所有 logger 配置
                val loggers = context.configuration.loggers

                loggers.forEach { (loggerName, loggerConfig) ->
                    loggerConfig.appenders.forEach { (appenderName, appender) ->
                        try {
                            // 特别处理 RollingFileAppender
                            if (appenderName.contains("File", ignoreCase = true)) {
                                emergencyInfo("刷新文件 Appender: $appenderName")
                                // 停止并重新启动以确保数据刷新
                                appender.stop()
                                // 短暂延迟确保文件操作完成
                                Thread.sleep(50)
                                appender.start()
                            }
                        } catch (e: Exception) {
                            System.err.println("❌ 刷新 Appender $appenderName 失败: ${e.message}")
                        }
                    }
                }
            }

            // 额外等待确保所有日志写入完成
            Thread.sleep(150)
            emergencyInfo("日志刷新完成")

        } catch (e: Exception) {
            System.err.println("❌ 刷新日志失败: ${e.message}")
        }
    }

    /**
     * 关闭 Log4j2 上下文 - 安全版本
     */
    private fun shutdownLog4j2() {
        try {
            emergencyInfo("正在关闭 Log4j2 上下文...")

            val context = LogManager.getContext(false)
            if (context is LoggerContext) {
                // 先停止所有 appender
                context.configuration.loggers.forEach { (_, loggerConfig) ->
                    loggerConfig.appenders.values.forEach { appender ->
                        try {
                            appender.stop()
                        } catch (e: Exception) {
                            // 忽略停止错误
                        }
                    }
                }

                // 等待一段时间确保文件操作完成
                Thread.sleep(100)

                // 关闭上下文
                context.stop()

                // 使用 Configurator 进行完全关闭
                Configurator.shutdown(context)
            }

            emergencyInfo("Log4j2 上下文关闭完成")

        } catch (e: Exception) {
            System.err.println("❌ 关闭 Log4j2 上下文失败: ${e.message}")
        }
    }

    /**
     * 同步信息输出（同时输出到控制台和日志）
     */
    fun syncInfo(message: String) {
        println("[INFO] $message")
        if (!isShuttingDown.get()) {
            logger.info(message)
        }
    }

    /**
     * 同步调试输出
     */
    fun syncDebug(message: String) {
        if (!isShuttingDown.get()) {
            logger.debug(message)
        }
    }

    /**
     * 同步错误输出
     */
    fun syncError(message: String, exception: Exception? = null) {
        System.err.println("[ERROR] $message")
        exception?.let { System.err.println("[ERROR] Exception: ${it.message}") }

        if (!isShuttingDown.get()) {
            if (exception != null) {
                logger.error(message, exception)
            } else {
                logger.error(message)
            }
        }
    }

    /**
     * 同步警告输出
     */
    fun syncWarn(message: String, exception: Exception? = null) {
        println("[WARN] $message")
        exception?.let { println("[WARN] Exception: ${it.message}") }

        if (!isShuttingDown.get()) {
            if (exception != null) {
                logger.warn(message, exception)
            } else {
                logger.warn(message)
            }
        }
    }

    /**
     * 紧急输出（始终输出到控制台，尝试记录日志）
     */
    fun emergencyInfo(message: String) {
        val formattedMessage = "[EMERGENCY] $message"
        println(formattedMessage)

        // 即使正在关闭也尝试记录到日志文件
        if (!isShuttingDown.get()) {
            try {
                logger.info(formattedMessage)
            } catch (e: Exception) {
                // 如果日志系统已经关闭，忽略错误
            }
        }
    }

    /**
     * 检查日志系统是否正在关闭
     */
    fun isLoggingShutdown(): Boolean = isShuttingDown.get()

    /**
     * 强制刷新当前日志
     */
    fun flushCurrentLogs() {
        if (!isShuttingDown.get()) {
            try {
                logger.info("手动刷新日志...")
                // Log4j2 通常会自动刷新，但可以强制调用
                val context = LogManager.getContext(false)
                if (context is LoggerContext) {
                    context.configuration.loggers.forEach { (_, loggerConfig) ->
                        loggerConfig.appenders.values.forEach { appender ->
                            try {
                                appender.stop()
                                Thread.sleep(10)
                                appender.start()
                            } catch (e: Exception) {
                                // 忽略错误
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                System.err.println("手动刷新日志失败: ${e.message}")
            }
        }
    }
}