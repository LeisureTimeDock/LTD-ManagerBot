package top.r3944realms.ltdmanager.core.config

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.introspector.PropertyUtils
import top.r3944realms.ltdmanager.utils.ConfigInitializer
import top.r3944realms.ltdmanager.utils.NamingConventionUtil
import java.nio.file.Files
import java.nio.file.Paths

object YamlConfigLoader {
    val configFilePath = Paths.get("config/application.yaml") // 配置文件路径
    private val _config by lazy { loadConfig() } // 延迟初始化
    val config: ConfigWrapper get() = _config

    init {
        // 第一次启动确保配置文件存在
        ConfigInitializer.initConfig("application.yaml", "config")

        // 初始化后加密（确保只执行一次）
        runCatching {
           ensureConfigEncrypted(_config)
        }.onFailure { e ->
            println("初始化加密失败: ${e.message}")
            e.printStackTrace()
        }
    }
    private fun ensureConfigEncrypted(config: ConfigWrapper?) {
        config?.database?.encryptPassword()
        config?.websocket?.encryptToken()
        config?.http?.encryptToken()
        config?.mcsm?.encryptApi()
        config?.mail?.encryptPassword()
        config?.tools?.rcon?.encryptPassword()
        config?.blessingSkinServer?.invitationApi?.encryptToken()
        config?.dgLab?.wsServer?.encryptPassword()
        config?.imgTu?.encryptPassword()
    }
    private fun loadConfig(): ConfigWrapper {
        if (!Files.exists(configFilePath)) {
            throw RuntimeException("配置文件未找到: $configFilePath")
        }

        try {
            val yamlContent = Files.readString(configFilePath)

            return Yaml(getConstructor()).load(yamlContent)
                ?: throw RuntimeException("YAML解析返回null")

        } catch (e: Exception) {
            throw RuntimeException("YAML解析失败: ${e.message}", e)
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
    fun loadMcsmConfig(): McsmConfig = config.mcsm
    fun loadWebsocketConfig(): WebsocketConfig = config.websocket
    fun loadHttpConfig(): HttpConfig = config.http
    fun loadModeConfig(): ModeConfig = config.mode
    fun loadToolConfig(): ToolConfig = config.tools
    fun loadMailConfig(): MailConfig = config.mail
    fun loadBlessingSkinServerConfig(): BlessingSkinServerConfig = config.blessingSkinServer
    fun loadDgLabConfig(): DgLabConfig = config.dgLab
    fun loadTuImgConfig(): ImgTuConfig = config.imgTu
    data class ConfigWrapper(
        var database: DatabaseConfig = DatabaseConfig(),
        var crypto: CryptoConfig = CryptoConfig(),
        var mode: ModeConfig = ModeConfig(),
        var websocket: WebsocketConfig = WebsocketConfig(),
        var http: HttpConfig = HttpConfig(),
        var tools: ToolConfig = ToolConfig(),
        var mail: MailConfig = MailConfig(),
        var mcsm: McsmConfig = McsmConfig(),
        var blessingSkinServer: BlessingSkinServerConfig = BlessingSkinServerConfig(),
        var dgLab: DgLabConfig = DgLabConfig(),
        var imgTu: ImgTuConfig = ImgTuConfig(),

    )
}