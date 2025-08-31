package top.r3944realms.ltdmanager

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.module.*


fun main() = GlobalManager.runBlockingMain {
    val groupId:Long = 538751386
    val selfQQId = 3327379836
    // 创建模块实例
    val groupModule = GroupRequestHandlerModule(
        client = GlobalManager.napCatClient,
        targetGroupId = groupId
    )
    val groupMsgPollingModule = GroupMessagePollingModule(
        targetGroupId = groupId,
        pollIntervalMillis = 5_000L,
        msgHistoryCheck = 15
    )
    val toolConfig = YamlConfigLoader.loadToolConfig()
    val rconModule = RconPlayerListModule(
        groupMessagePollingModule = groupMsgPollingModule,
        rconTimeOut = 2_000L,
        cooldownMillis = 10_000L,
        selfId = selfQQId,
        selfNickName = "闲趣老土豆",
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
                host = mailConfig.host.toString(),
                authToken = mailConfig.decryptedPassword.toString(),
                port = mailConfig.port!!,
                senderEmailAddress = mailConfig.mailAddress!!,
    )
    val blessingSkinConfig = YamlConfigLoader.loadBlessingSkinServerConfig()
    val invitationCodesModule = InvitationCodesModule(
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

    // 注册模块到全局模块管理器
    GlobalManager.moduleManager.registerModule(groupModule)
    GlobalManager.moduleManager.registerModule(groupMsgPollingModule)
    GlobalManager.moduleManager.registerModule(rconModule)
    GlobalManager.moduleManager.registerModule(mailModule)
    GlobalManager.moduleManager.registerModule(invitationCodesModule)

    // 加载模块
    GlobalManager.moduleManager.loadModule(groupModule.name)
    GlobalManager.moduleManager.loadModule(groupMsgPollingModule.name)
    GlobalManager.moduleManager.loadModule(rconModule.name)
    GlobalManager.moduleManager.loadModule(mailModule.name)
    GlobalManager.moduleManager.loadModule(invitationCodesModule.name)
}