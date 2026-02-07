package top.r3944realms.ltdmanager.module

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.core.config.ModuleConfig
import top.r3944realms.ltdmanager.core.config.ModuleConfig.Module.ModuleType.*
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.module.exception.ConfigError

object ModuleFactory {
    fun createModule(config: ModuleConfig.Module): BaseModule {
        return when(config.type) {
            GROUP_MESSAGE_POLLING_MODULE -> createGroupMessagePolling(config)
            GROUP_REQUEST_HANDLER_MODULE -> createGroupRequestHandler(config)
            MAIL_MODULE -> createMail(config)
            BAN_MODULE -> createBan(config)
            DG_LAB_MODULE -> createDgLab(config)
            INVITE_MODULE -> createInvite(config)
            MC_SERVER_STATUS_MODULE -> createMcServerStatus(config)
            RCON_PLAYER_LIST_MODULE -> createRconPlayerList(config)
            STATE_MODULE -> createState(config)
            MOD_GROUP_HANDLER_MODULE -> createModGroupHandler(config)
            HELP_MODULE -> createHelpModule(config)
        }
    }
    private fun resolveDependency(dep: ModuleConfig.Module.Dependency?, name: String): BaseModule? {
        if (dep != null) {
            return GlobalManager.moduleManager.getModule(dep.getDepName())
        } else throw ConfigError (ConfigError.Type.MISSING_PARAMETER, "dependency", name)
    }
    private fun createGroupMessagePolling(config: ModuleConfig.Module): GroupMessagePollingModule {
        val targetGroupId = config.long("target-group-id")
        val pollIntervalMillis = config.getOrDefault("poll-interval-millis", 5_000L)
        val msgHistoryCheck = config.getOrDefault("msg-history-check", 15)
        return GroupMessagePollingModule(
            config.name, targetGroupId,
            pollIntervalMillis, msgHistoryCheck,
        )
    }
    private fun createGroupRequestHandler(config: ModuleConfig.Module): GroupRequestHandlerModule {
        val targetGroupId = config.long("target-group-id")
        val pollIntervalMillis = config.getOrDefault("poll-interval-millis", 30_000L)
        return GroupRequestHandlerModule(
            config.name, GlobalManager.napCatClient,
            targetGroupId, pollIntervalMillis
        )
    }

    private fun createMail(config: ModuleConfig.Module): MailModule {
        val mailConfig = YamlConfigLoader.loadMailConfig()
        return MailModule(
            moduleName = config.name,
            host = mailConfig.host.toString(),
            authToken = mailConfig.decryptedPassword.toString(),
            port = mailConfig.port!!,
            senderEmailAddress = mailConfig.mailAddress!!
        )
    }

    private fun createBan(config: ModuleConfig.Module): BanModule {
        val selfId = config.long("self-id")
        val adminIds = config.list<Long>("admin-ids")
        val muteCommandPrefixList = config.stringList("mute-command-prefix-list")
        val unmuteCommandPrefixList = config.stringList("unmute-command-prefix-list")
        val groupMessagePollingModule = resolveDependency(config.findDependency(GROUP_MESSAGE_POLLING_MODULE), "groupMessagePolling") as GroupMessagePollingModule
        val minBanMinutes = config.int("min-ban-minutes")
        val maxBanMinutes = config.int("max-ban-minutes")
        val factorX: Int = config.int("factor-x")
        return BanModule(
            config.name,
            groupMessagePollingModule,
            selfId,
            adminIds,
            muteCommandPrefixList,
            unmuteCommandPrefixList,
            minBanMinutes,
            maxBanMinutes,
            factorX
        )
    }

    private fun createDgLab(config: ModuleConfig.Module): DGLabModule {
        val selfId = config.long("self-id")
        val adminIds = config.list<Long>("admin-ids")
        val maxClientNumber = config.int("max-client-number")
        val commandHead = config.stringList("command-head")
        val groupMessagePollingModule = resolveDependency(config.findDependency(GROUP_MESSAGE_POLLING_MODULE), "groupMessagePolling") as GroupMessagePollingModule
        return DGLabModule(
            config.name,
            groupMessagePollingModule,
            selfId,
            adminIds,
            maxClientNumber,
            commandHead
        )
    }

    private fun createInvite(config: ModuleConfig.Module): InvitationCodesModule {
        val selfId = config.long("self-id")
        val groupMessagePollingModule = resolveDependency(config.findDependency(GROUP_MESSAGE_POLLING_MODULE), "groupMessagePolling") as GroupMessagePollingModule
        val mailModule = resolveDependency(config.findDependency(MAIL_MODULE), "mailModule") as MailModule
        val blessingSkinConfig = YamlConfigLoader.loadBlessingSkinServerConfig()
        val cooldownMillis = config.getOrDefault("cooldown-millis", 120_000L)
        val keywords = config.stringList("keywords")
        return InvitationCodesModule(
            config.name,
            groupMessagePollingModule,
            mailModule,
            blessingSkinConfig.invitationApi?.decryptedToken!!,
            selfId,
            cooldownMillis,
            keywords.toSet()
        )
    }

    private fun createMcServerStatus(config: ModuleConfig.Module): McServerStatusModule {
        val selfId = config.long("self-id")
        val cooldownMillis = config.getOrDefault("cooldown-millis", 60_000L)
        val groupMessagePollingModule = resolveDependency(config.findDependency(GROUP_MESSAGE_POLLING_MODULE), "groupMessagePolling") as GroupMessagePollingModule
        val commands = config.stringList("commands")
        val selfNickName = config.string("self-nick-name")
        val preset = config.map<Set<String>, String>("preset-server")
        return McServerStatusModule(
            config.name,
            groupMessagePollingModule,
            selfId,
            selfNickName,
            cooldownMillis,
            commands,
            preset
        )
    }

    private fun createRconPlayerList(config: ModuleConfig.Module): RconPlayerListModule {
        val toolConfig = YamlConfigLoader.loadToolConfig()
        val selfId = config.long("self-id")
        val cooldownMillis = config.getOrDefault("cooldown-millis", 10_000L)
        val rconTimeout = config.getOrDefault("rcon-timeout-millis", 2_000L)
        val groupMessagePollingModule = resolveDependency(config.findDependency(GROUP_MESSAGE_POLLING_MODULE), "groupMessagePolling") as GroupMessagePollingModule
        val selfNickName = config.string("self-nick-name")
        val keywords = config.stringList("keywords")
        return RconPlayerListModule(
            config.name,
            groupMessagePollingModule,
            rconTimeout,
            cooldownMillis,
            selfId,
            selfNickName,
            toolConfig.rcon.mcRconToolPath.toString(),
            toolConfig.rcon.mcRconToolConfigPath.toString(),
            keywords.toSet()
        )
    }

    private fun createState(config: ModuleConfig.Module): StateModule {
        val onlineName = config.string("online-name")
        val offlineName = config.string("offline-name")
        return StateModule(
            config.name,
            onlineName,
            offlineName
        )
    }

    private fun createModGroupHandler(config: ModuleConfig.Module): ModGroupHandlerModule {
        val targetGroupId = config.long("target-group-id")
        val answers = config.stringList("answers")
        val pollingMillis = config.getOrDefault("poll-interval-millis", 30_000L)
        return ModGroupHandlerModule(
            config.name,
            targetGroupId,
            answers,
            pollingMillis
        )
    }

    private fun createHelpModule(config: ModuleConfig.Module): HelpModule {
        val selfId = config.long("self-id")
        val cooldownMillis = config.getOrDefault("cooldown-millis", 10_000L)
        config.getOrDefault("rcon-timeout-millis", 2_000L)
        val groupMessagePollingModule = resolveDependency(config.findDependency(GROUP_MESSAGE_POLLING_MODULE), "groupMessagePolling") as GroupMessagePollingModule
        val selfNickName = config.string("self-nick-name")
        val keywords = config.stringList("keywords")
        return HelpModule(
            config.name,
            groupMessagePollingModule,
            selfId,
            selfNickName,
            keywords,
            cooldownMillis
        )
    }

}