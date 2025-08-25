package top.r3944realms.ltdmanager.utils

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader

enum class Environment {
    DEVELOPMENT,
    DEBUG,
    PRODUCTION,
    ;
    companion object {
        private val environment: Environment?
            get() = YamlConfigLoader.loadModeConfig().environment
        fun isDevelopment(): Boolean {
            return environment == DEVELOPMENT
        }
        fun isProduction(): Boolean {
            return environment == PRODUCTION
        }
        fun isDebug(): Boolean {
            return environment == DEBUG
        }
    }
}