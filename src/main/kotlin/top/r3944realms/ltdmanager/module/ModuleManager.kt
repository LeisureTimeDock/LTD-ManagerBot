package top.r3944realms.ltdmanager.module

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.util.*

class ModuleManager {
    private val modules = mutableMapOf<String, BaseModule>()
    private var hasLoaded = false
    fun getModules(): Map<String, BaseModule> {
        return (modules).toMap()
    }
    fun getModule(name: String): BaseModule? {
        return modules[name]
    }
    fun loadConfig() {
        if (!hasLoaded) {
            hasLoaded = true
            val moduleConfig = YamlConfigLoader.loadModuleConfig()
            moduleConfig.modules.let {
                val enableBaseModules = LinkedList<BaseModule>()
                if (it != null) {
                    for (mod in it) {
                        val module = ModuleFactory.createModule(mod)
                        register(module)
                        if (mod.enabled) {
                            enableBaseModules.add(module)
                        }
                    }
                }
                for (module in enableBaseModules) {
                    load(module.name)
                }
            }
        }
    }
    /**
     * 注册模块到管理器
     */
    fun register(module: BaseModule) {
        if (modules.containsKey(module.name)) {
            LoggerUtil.logger.warn("模块已注册: ${module.name}")
            return
        }
        modules[module.name] = module
        LoggerUtil.logger.info("模块注册: ${module.name}")
    }
    /**
     * 注册多模块到管理器
     */
    fun register(moduleList: List<BaseModule>) {
        for (module in moduleList) {
            register(module)
        }
    }

    /**
     * 加载指定模块
     */
    fun load(name: String) {
        val module = modules[name]
        if (module == null) {
            LoggerUtil.logger.warn("尝试加载不存在的模块: $name")
            return
        }
        if (module.loaded) {
            LoggerUtil.logger.info("模块已加载: $name")
            return
        }
        try {
            module.load()
        } catch (e: Exception) {
            LoggerUtil.logger.error("加载模块 $name 失败", e)
        }
    }

    /**
     * 卸载指定模块
     */
    suspend fun unload(name: String) {
        val module = modules[name]
        if (module == null) {
            LoggerUtil.logger.warn("尝试卸载不存在的模块: $name")
            return
        }
        if (!module.loaded) {
            LoggerUtil.logger.info("模块未加载: $name")
            return
        }
        try {
            module.unload()
        } catch (e: Exception) {
            LoggerUtil.logger.warn("卸载模块 $name 失败", e)
        }
    }

    /**
     * 卸载所有模块
     */
    suspend fun unloadAll() {
        modules.values.forEach { module ->
            try {
                if (module.loaded) {
                    module.unload()

                }
            } catch (e: Exception) {
                LoggerUtil.logger.warn("卸载模块 ${module.name} 失败", e)
            }
        }
    }

    /**
     * 提供获取所有模块信息的方法
     */
    fun getAllModuleInfo(): Map<String, String> {
        return modules.mapValues { it.value.info() }
    }

    /**
     * 获取所有模块名称
     */
    fun getModuleNames(): List<String> = modules.keys.toList()

    /**
     * 检查模块是否已加载
     */
    fun isModuleLoaded(name: String): Boolean {
        return modules[name]?.loaded ?: false
    }
    /**
     * 扩展方法：批量加载模块
     */
    fun ModuleManager.loadModules(vararg names: String) {
        names.forEach { load(it) }
    }

    /**
     * 扩展方法：批量卸载模块
     */
    suspend fun ModuleManager.unloadModules(vararg names: String) {
        names.forEach { unload(it) }
    }
    /**
     * 关闭所有模块
     */
    suspend fun stopAllModules() {
        modules.values.forEach { module ->
            if (module.loaded) {
                module.stop()
            }
        }
    }
}