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
            } else {
                // 资源文件不存在，可写入内置默认 YAML
                val defaultYaml = """
                database:
                  url: "jdbc:mysql://localhost:3306/quizdb?useSSL=false&serverTimezone=UTC"
                  user: "root"
                  encrypted-password: "123123aa"
                crypto:
                  secret-key: "ltd25r3944realms"
                mode:
                  bot-api-type: HTTP
                  environment: DEVELOPMENT
                http:
                  url: "https://127.0.0.1:3001"
                  encrypted-token: "123123bb"
                websocket:
                  url: "wss://127.0.0.1:3002"
                  encrypted-token: "123123cc"
                tools:
                  rcon:
                    mc-rcon-tool-path: "/path/to/rcon"
                    mc-rcon-tool-config-path: "/path/to/rcon_config"
                    server-url: "your.minecraft.server"
                    rcon-password: "123123dd"
                """.trimIndent()

                Files.writeString(filePath, defaultYaml)
                LoggerUtil.logger.info("已生成默认配置文件(使用内置内容): $filePath")
            }
            LoggerUtil.logger.info("第一次启动，请修改配置后再启动")
            exitProcess(-1);
        } else {
            LoggerUtil.logger.info("配置文件已存在: $filePath")
        }
    }
}