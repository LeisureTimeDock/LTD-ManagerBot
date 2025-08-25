package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.ApiType
import top.r3944realms.ltdmanager.utils.Environment

data class ModeConfig(
    var botApiType: ApiType? = null,
    var environment: Environment? = null,
) {
    override fun toString(): String {
        return "ModeConfig(botApiType=$botApiType, environment=$environment)"
    }
}