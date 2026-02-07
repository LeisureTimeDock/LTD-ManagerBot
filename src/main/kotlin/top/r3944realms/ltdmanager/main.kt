package top.r3944realms.ltdmanager

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.module.*

// DSL
fun main() = GlobalManager.runBlockingMain {
    GlobalManager.initApplication()
//    val commonGroupId:Long = 538751386
//    val whitelistGroupId:Long = 920719236
//    val selfQQId = 3327379836
//    val selfNickName = "闲趣老土豆"
//    // 创建模块实例
//    val groupModule = GroupRequestHandlerModule(
//        moduleName = "WhiteListGroup",
//        client = GlobalManager.napCatClient,
//        targetGroupId = whitelistGroupId
//    )
//    val commonGroupMsgPollingModule = GroupMessagePollingModule(
//        moduleName = "CommonGroupMsgPolling",
//        targetGroupId = commonGroupId,
//        pollIntervalMillis = 5_000L,
//        msgHistoryCheck = 15
//    )
//    val whiteListGroupMsgPollingModule = GroupMessagePollingModule(
//        moduleName = "WhiteListGroup",
//        targetGroupId = whitelistGroupId,
//        pollIntervalMillis = 5_000L,
//        msgHistoryCheck = 15
//    )
//    val commonHelpModule = HelpModule(
//        moduleName = "CommonGroup",
//        keywords = listOf("help", "帮助"),
//        groupMessagePollingModule = commonGroupMsgPollingModule,
//        selfId = selfQQId,
//        selfNickName = selfNickName,
//    )
//    val whitelistHelpModule = HelpModule(
//        moduleName = "WhiteListGroup",
//        keywords = listOf("help", "帮助"),
//        groupMessagePollingModule = whiteListGroupMsgPollingModule,
//        selfId = selfQQId,
//        selfNickName = selfNickName,
//    )
//    val toolConfig = YamlConfigLoader.loadToolConfig()
//    val corconModule = RconPlayerListModule(
//        moduleName = "CommonGroup",
//        groupMessagePollingModule = commonGroupMsgPollingModule,
//        rconTimeOut = 2_000L,
//        cooldownMillis = 10_000L,
//        selfId = selfQQId,
//        selfNickName = selfNickName,
//        rconPath = toolConfig.rcon.mcRconToolPath.toString(),
//        rconConfigPath = toolConfig.rcon.mcRconToolConfigPath.toString(),
//        keywords = setOf(
//            //形容
//            "土豆", "马铃薯", "Potato", "potato", "POTATO",
//            "Potatoes", "potatoes", "POTATOES", "🥔",
//            //正经
//            "列表","服务器状态", "TPS", "tps", "list", "List"
//        )
//    )
//    val rconModule = RconPlayerListModule(
//        moduleName = "WhiteListGroup",
//        groupMessagePollingModule = whiteListGroupMsgPollingModule,
//        rconTimeOut = 2_000L,
//        cooldownMillis = 10_000L,
//        selfId = selfQQId,
//        selfNickName = selfNickName,
//        rconPath = toolConfig.rcon.mcRconToolPath.toString(),
//        rconConfigPath = toolConfig.rcon.mcRconToolConfigPath.toString(),
//        keywords = setOf(
//            //形容
//            "土豆", "马铃薯", "Potato", "potato", "POTATO",
//            "Potatoes", "potatoes", "POTATOES", "🥔",
//            //正经
//            "列表","服务器状态", "TPS", "tps", "list", "List"
//        )
//    )
////    val mailConfig = YamlConfigLoader.loadMailConfig()
////    val mailModule = MailModule(
////        moduleName = "WhiteListGroup",
////        host = mailConfig.host.toString(),
////        authToken = mailConfig.decryptedPassword.toString(),
////        port = mailConfig.port!!,
////        senderEmailAddress = mailConfig.mailAddress!!,
////    )
////    val blessingSkinConfig = YamlConfigLoader.loadBlessingSkinServerConfig()
////    val invitationCodesModule = InvitationCodesModule(
////        moduleName = "WhiteListGroup",
////        groupMessagePollingModule = commonGroupMsgPollingModule,
////        mailModule = mailModule,
////        apiToken = blessingSkinConfig.invitationApi?.decryptedToken!!,
////        selfId = selfQQId,
////        keywords = setOf(
////            "申请皮肤站注册邀请码",
////            "申请土豆服务器注册邀请码",
////            "申请LTD邀请码",
////            "Apply for an invitation code"
////        )
////    )
////    val commonMcServerStatusModule = McServerStatusModule(
////        moduleName = "CommonGroup",
////        groupMessagePollingModule = commonGroupMsgPollingModule,
////        selfId = selfQQId,
////        cooldownMillis = 20_000L,
////        selfNickName = selfNickName,
////        commands = listOf("/m", "/mcs", "seek", "s", "test"),
////        presetServer = mapOf(
////            setOf("老土豆", "七周目", "7" ,"ZZ", "zz", "Zz", "seven") to "main.mmccdd.top:11106",
////            setOf("土豆", "八周目", "8" ,"39", "eight") to "ac.r3944realms.top"
////        )
////    )
////    val whitelistMcServerStatusModule = McServerStatusModule(
////        moduleName = "WhiteListGroup",
////        groupMessagePollingModule = whiteListGroupMsgPollingModule,
////        selfId = selfQQId,
////        cooldownMillis = 20_000L,
////        selfNickName = selfNickName,
////        commands = listOf("/m", "/mcs", "seek", "s", "test"),
////        presetServer = mapOf(
////            setOf("老土豆", "七周目", "7" ,"ZZ", "zz", "Zz", "seven") to "main.mmccdd.top:11106",
////            setOf("土豆", "八周目", "8" ,"39", "eight") to "ac.r3944realms.top"
////        )
////    )
//    val dgLabModule = DGLabModule(
//        moduleName = "DG",
//        groupMessagePollingModule = commonGroupMsgPollingModule,
//        selfId = selfQQId,
//        adminIds = listOf(2561098830L),
//        commandHead = listOf("dglab")
//    )
//
//    // 注册模块到全局模块管理器
//    GlobalManager.moduleManager.register(groupModule)
//    GlobalManager.moduleManager.register(commonGroupMsgPollingModule)
//    GlobalManager.moduleManager.register(whiteListGroupMsgPollingModule)
////    GlobalManager.moduleManager.registerModule(commonMcServerStatusModule)
//    GlobalManager.moduleManager.register(rconModule)
//    GlobalManager.moduleManager.register(corconModule)
////    GlobalManager.moduleManager.registerModule(whitelistMcServerStatusModule)
////    GlobalManager.moduleManager.registerModule(mailModule)
////    GlobalManager.moduleManager.registerModule(invitationCodesModule)
//    GlobalManager.moduleManager.register(whitelistHelpModule)
//    GlobalManager.moduleManager.register(commonHelpModule)
//    GlobalManager.moduleManager.register(dgLabModule)
////    GlobalManager.moduleManager.registerModule(banModule)
////    GlobalManager.moduleManager.registerModule(modGroupHandlerModule)
//
//    // 加载模块
//    GlobalManager.moduleManager.load(groupModule.name)
//    GlobalManager.moduleManager.load(commonGroupMsgPollingModule.name)
//    GlobalManager.moduleManager.load(whiteListGroupMsgPollingModule.name)
////    GlobalManager.moduleManager.loadModule(commonMcServerStatusModule.name)
//    GlobalManager.moduleManager.load(corconModule.name)
//    GlobalManager.moduleManager.load(rconModule.name)
////    GlobalManager.moduleManager.loadModule(mailModule.name)
////    GlobalManager.moduleManager.loadModule(invitationCodesModule.name)
//    GlobalManager.moduleManager.load(commonHelpModule.name)
////    GlobalManager.moduleManager.loadModule(whitelistMcServerStatusModule.name)
//    GlobalManager.moduleManager.load(whitelistHelpModule.name)
//    GlobalManager.moduleManager.load(dgLabModule.name)
////    GlobalManager.moduleManager.loadModule(banModule.name)
////    GlobalManager.moduleManager.loadModule(modGroupHandlerModule.name)
}