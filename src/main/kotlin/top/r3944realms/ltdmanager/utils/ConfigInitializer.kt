package top.r3944realms.ltdmanager.utils

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

object ConfigInitializer {

    /**
     * 初始化配置文件
     * @param fileName YAML 文件名，如 application.yml
     * @param configDir 配置目录，如 config
     */
    fun initConfig(fileName: String = "application.yml", configDir: String = "config") {
        val dirPath = Paths.get(configDir)
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath)
            LoggerUtil.logger.info("已创建配置目录: $configDir")
        }

        val filePath = dirPath.resolve(fileName)

        if (!Files.exists(filePath)) {
            // 从 resources 复制默认配置
            val resourceStream = YamlConfigLoader::class.java.classLoader.getResourceAsStream(fileName)
            if (resourceStream != null) {
                Files.copy(resourceStream, filePath, StandardCopyOption.REPLACE_EXISTING)
                LoggerUtil.logger.info("已生成默认配置文件: $filePath")
                LoggerUtil.logger.info("第一次启动，请修改配置后再启动")
                exitProcess(-1);
            } else throw Error("Jar内部资源文件缺失")

        } else {
            LoggerUtil.logger.info("配置文件已存在: $filePath")
        }
    }
}