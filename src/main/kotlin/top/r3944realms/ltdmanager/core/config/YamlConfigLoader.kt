package top.r3944realms.ltdmanager.core.config

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.introspector.PropertyUtils
import top.r3944realms.ltdmanager.utils.ConfigInitializer
import top.r3944realms.ltdmanager.utils.NamingConventionUtil
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object YamlConfigLoader {
    val appConfigFilePath: Path = Paths.get("config/application.yaml") // 配置文件路径
    val moduleConfigFilePath: Path = Paths.get("config/module.yaml") // 配置文件路径
    private val _app_config by lazy { loadAppConfigWrapper() } // 延迟初始化
    val appConfig: AppConfigWrapper get() = _app_config
    private val _module_config by lazy { loadModuleConfigWrapper() } // 延迟初始化
    val moduleConfig: ModuleConfigWrapper get() = _module_config

    init {
        // 第一次启动确保配置文件存在
        ConfigInitializer.initConfig("module.yaml", "config", false)
        ConfigInitializer.initConfig("application.yaml", "config")
        // 初始化后加密（确保只执行一次）
        runCatching {
           ensureConfigEncrypted(_app_config)
        }.onFailure { e ->
            println("初始化加密失败: ${e.message}")
            e.printStackTrace()
        }
    }
    private fun ensureConfigEncrypted(config: AppConfigWrapper?) {
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
    private fun loadAppConfigWrapper(): AppConfigWrapper {
        if (!Files.exists(appConfigFilePath)) {
            throw RuntimeException("应用配置文件未找到: $appConfigFilePath")
        }

        try {
            val yamlContent = Files.readString(appConfigFilePath)

            return Yaml(getConstructor(AppConfigWrapper::class.java)).load(yamlContent)
                ?: throw RuntimeException("YAML解析返回null")

        } catch (e: Exception) {
            throw RuntimeException("YAML解析失败: ${e.message}", e)
        }

    }
    private fun loadModuleConfigWrapper(): ModuleConfigWrapper {
        if (!Files.exists(moduleConfigFilePath)) {
            throw RuntimeException("模块配置文件未找到: $moduleConfigFilePath")
        }

        try {
            val yamlContent = Files.readString(moduleConfigFilePath)

            return Yaml(getConstructor(ModuleConfigWrapper::class.java)).load(yamlContent)
                ?: throw RuntimeException("YAML解析返回null")

        } catch (e: Exception) {
            throw RuntimeException("YAML解析失败: ${e.message}", e)
        }

    }
    private fun getConstructor(clazz: Class<*>): Constructor {
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

        return Constructor(clazz, LoaderOptions()).apply {
            setPropertyUtils(propertyUtils)
        }
    }

    fun loadDatabaseConfig(): DatabaseConfig = appConfig.database
    fun loadCryptoConfig(): CryptoConfig = appConfig.crypto
    fun loadMcsmConfig(): McsmConfig = appConfig.mcsm
    fun loadWebsocketConfig(): WebsocketConfig = appConfig.websocket
    fun loadHttpConfig(): HttpConfig = appConfig.http
    fun loadModeConfig(): ModeConfig = appConfig.mode
    fun loadToolConfig(): ToolConfig = appConfig.tools
    fun loadMailConfig(): MailConfig = appConfig.mail
    fun loadBlessingSkinServerConfig(): BlessingSkinServerConfig = appConfig.blessingSkinServer
    fun loadDgLabConfig(): DgLabConfig = appConfig.dgLab
    fun loadTuImgConfig(): ImgTuConfig = appConfig.imgTu
    fun loadModuleConfig(): ModuleConfig = moduleConfig.module
    data class AppConfigWrapper(
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

    data class ModuleConfigWrapper(
        var module: ModuleConfig = ModuleConfig(),
    )
}