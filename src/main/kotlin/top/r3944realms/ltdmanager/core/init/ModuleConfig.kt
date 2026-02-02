package top.r3944realms.ltdmanager.core.config


data class ModuleConfig(
    val name: String,
    val type: ModuleType,
    val enabled: Boolean,
    val
) {
    data class Dependency(
        val moduleName: String,     // 依赖的模块名称
        val type: DependencyType,   // 依赖类型
        val required: Boolean = true  // 是否必需
    )
    enum class ModuleType {
        GROUP_MESSAGE_POLLING_MODULE,
        GROUP_REQUEST_HANDLER_MODULE,
        MAIL_MODULE,
        BAN_MODULE,
        DG_LAB_MODULE,
        INVITE_MODULE,
        MC_SERVER_STATUS_MODULE,
        RCON_PLAYER_LIST_MODULE,
        STATE_MODULE
    }
}
