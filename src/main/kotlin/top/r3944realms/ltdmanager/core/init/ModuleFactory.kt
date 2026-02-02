package top.r3944realms.ltdmanager.core.init

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.core.init.ModuleConfig.ModuleType.*
import top.r3944realms.ltdmanager.module.BaseModule
import top.r3944realms.ltdmanager.module.GroupRequestHandlerModule

object ModuleFactory {
    fun createModule(config: ModuleConfig): BaseModule {
        return when(config.type) {
            GROUP_MESSAGE_POLLING_MODULE -> TODO()
            GROUP_REQUEST_HANDLER_MODULE -> createGroupRequestHandler(config)
            MAIL_MODULE -> TODO()
            BAN_MODULE -> TODO()
            DG_LAB_MODULE -> TODO()
            INVITE_MODULE -> TODO()
            MC_SERVER_STATUS_MODULE -> TODO()
            RCON_PLAYER_LIST_MODULE -> TODO()
            STATE_MODULE -> TODO()
            MOD_GROUP_HANDLER_MODULE -> TODO()
            HELP_MODULE -> TODO()
        }
    }
    private fun createGroupRequestHandler(config: ModuleConfig): GroupRequestHandlerModule {
        val targetGroupId = config.long("targetGroupId")
        val pollIntervalMillis = config.getOrDefault("pollIntervalMillis", 30_000L)
        return GroupRequestHandlerModule(
            config.name, GlobalManager.napCatClient,
            targetGroupId, pollIntervalMillis
        )
    }
}