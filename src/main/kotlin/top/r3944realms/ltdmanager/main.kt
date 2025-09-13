package top.r3944realms.ltdmanager

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.module.McServerStatusModule
import top.r3944realms.ltdmanager.module.*


fun main() = GlobalManager.runBlockingMain {
    val groupId:Long = 538751386
    val selfQQId = 3327379836
    val selfNickName = "闲趣老土豆"
    // 创建模块实例
    val groupModule = GroupRequestHandlerModule(
        moduleName = "WhiteListGroup",
        client = GlobalManager.napCatClient,
        targetGroupId = groupId
    )
    val groupMsgPollingModule = GroupMessagePollingModule(
        moduleName = "WhiteListGroup",
        targetGroupId = groupId,
        pollIntervalMillis = 5_000L,
        msgHistoryCheck = 15
    )
    val helpModule = HelpModule(
        moduleName = "WhiteListGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        selfId = selfQQId,
        selfNickName = selfNickName,
    )
    val toolConfig = YamlConfigLoader.loadToolConfig()
    val rconModule = RconPlayerListModule(
        moduleName = "WhiteListGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        rconTimeOut = 2_000L,
        cooldownMillis = 10_000L,
        selfId = selfQQId,
        selfNickName = selfNickName,
        rconPath = toolConfig.rcon.mcRconToolPath.toString(),
        rconConfigPath = toolConfig.rcon.mcRconToolConfigPath.toString(),
        keywords = setOf(
            //形容
            "土豆", "马铃薯", "Potato", "potato", "POTATO",
            "Potatoes", "potatoes", "POTATOES", "🥔",
            //正经
            "列表","服务器状态", "TPS", "tps", "list", "List"
        )
    )
    val mailConfig = YamlConfigLoader.loadMailConfig()
    val mailModule = MailModule(
        moduleName = "WhiteListGroup",
        host = mailConfig.host.toString(),
        authToken = mailConfig.decryptedPassword.toString(),
        port = mailConfig.port!!,
        senderEmailAddress = mailConfig.mailAddress!!,
    )
    val blessingSkinConfig = YamlConfigLoader.loadBlessingSkinServerConfig()
    val invitationCodesModule = InvitationCodesModule(
        moduleName = "WhiteListGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        mailModule = mailModule,
        apiToken = blessingSkinConfig.invitationApi?.decryptedToken!!,
        selfId = selfQQId,
        keywords = setOf(
            "申请皮肤站注册邀请码",
            "申请土豆服务器注册邀请码",
            "申请LTD邀请码",
            "Apply for an invitation code"
        )
    )
    val mcServerStatusModule = McServerStatusModule(
        moduleName = "WhiteListGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        selfId = selfQQId,
        cooldownMillis = 20_000L,
        selfNickName = selfNickName,
        commands = listOf("/m", "/mcs", "seek", "s"),
        presetServer = mapOf(
            setOf("先行土豆", "先行", "pre", "Pre", "BF", "bf", "p", "P") to "n2.akiracloud.net:10599",
            setOf("土豆", "老土豆", "七周目", "7" ,"ZZ", "zz", "Zz", "seven") to "main.mmccdd.top:11106",
        )
    )
    val banModule = BanModule(
        moduleName = "WhiteListGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        selfId = selfQQId,
        commandPrefixList = listOf("口球", "mute", "杂鱼三九"),
        minBanMinutes = 1,
        maxBanMinutes = 15,
    )
    val modGroupHandlerModule = ModGroupHandlerModule(
        moduleName = "ModGroup",
        targetGroupId = 339340846,
        answers = listOf("戏鸢", "一只戏鸢", "折戏鸢", "LostInLinearPast", "lostinlinearpast"),
        pollIntervalMillis = 15_000L,
    )

    // 注册模块到全局模块管理器
    GlobalManager.moduleManager.registerModule(groupModule)
    GlobalManager.moduleManager.registerModule(groupMsgPollingModule)
    GlobalManager.moduleManager.registerModule(mcServerStatusModule)
    GlobalManager.moduleManager.registerModule(rconModule)
    GlobalManager.moduleManager.registerModule(mailModule)
    GlobalManager.moduleManager.registerModule(invitationCodesModule)
    GlobalManager.moduleManager.registerModule(helpModule)
    GlobalManager.moduleManager.registerModule(banModule)
    GlobalManager.moduleManager.registerModule(modGroupHandlerModule)

    // 加载模块
    GlobalManager.moduleManager.loadModule(groupModule.name)
    GlobalManager.moduleManager.loadModule(groupMsgPollingModule.name)
    GlobalManager.moduleManager.loadModule(mcServerStatusModule.name)
    GlobalManager.moduleManager.loadModule(rconModule.name)
    GlobalManager.moduleManager.loadModule(mailModule.name)
    GlobalManager.moduleManager.loadModule(invitationCodesModule.name)
    GlobalManager.moduleManager.loadModule(helpModule.name)
    GlobalManager.moduleManager.loadModule(banModule.name)
    GlobalManager.moduleManager.loadModule(modGroupHandlerModule.name)
}