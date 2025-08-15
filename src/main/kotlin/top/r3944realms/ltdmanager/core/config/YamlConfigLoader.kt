package top.r3944realms.ltdmanager.core.config

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.introspector.PropertyUtils
import top.r3944realms.ltdmanager.utils.NamingConventionUtil

object YamlConfigLoader {
    private val config: ConfigWrapper = loadConfig().also {
        ensureConfigEncrypted(it) // 初始化后立即加密
    }
    private fun ensureConfigEncrypted(config: ConfigWrapper?) {
        config?.database?.encryptPassword()
    }
    private fun loadConfig(): ConfigWrapper {
        YamlConfigLoader::class.java.classLoader.getResourceAsStream("application.yaml").use { inputStream ->
            if (inputStream == null) {
                throw RuntimeException("配置文件 application.yaml 未找到！")
            }
            return Yaml(getConstructor()).load(inputStream)
        }
    }
    private fun getConstructor(): Constructor {
        val propertyUtils = object : PropertyUtils() {
            override fun getProperty(type: Class<*>, name: String): Property {
                val processedName = if (name.contains("-")) {
                    NamingConventionUtil.hyphenToCamel(name) // 连字符转驼峰
                } else {
                    name
                }
                return super.getProperty(type, processedName)
            }
        }

        return Constructor(ConfigWrapper::class.java, LoaderOptions()).apply {
            setPropertyUtils(propertyUtils)
        }
    }
    fun loadDatabaseConfig(): DatabaseConfig = config.database
    fun loadCryptoConfig(): CryptoConfig = config.crypto
    fun loadWebsocketConfig(): WebsocketConfig = config.websocket
    data class ConfigWrapper(
        var database :DatabaseConfig,
        var crypto :CryptoConfig,
        var websocket :WebsocketConfig
    )
}