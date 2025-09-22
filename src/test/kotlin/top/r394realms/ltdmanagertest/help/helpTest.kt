package top.r394realms.ltdmanagertest.help

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.module.BanModule
import top.r3944realms.ltdmanager.module.GroupMessagePollingModule
import top.r3944realms.ltdmanager.module.HelpModule

fun main() = GlobalManager.runBlockingMain {
    val groupId:Long = 920719236
    val selfQQId = 3327379836
    val selfNickName = "闲趣老土豆"
    // 创建模块实例
    val groupMsgPollingModule = GroupMessagePollingModule(
        moduleName = "TestGroup",
        targetGroupId = groupId,
        pollIntervalMillis = 5_000L,
        msgHistoryCheck = 15
    )
    val helpModule = HelpModule(
        moduleName = "TestGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        selfId = selfQQId,
        selfNickName = selfNickName,
    )
    val banModule = BanModule(
        moduleName = "TestGroup",
        groupMessagePollingModule = groupMsgPollingModule,
        selfId = selfQQId,
        adminsId = listOf(2561098830),
        muteCommandPrefixList = listOf("禁言", "口球", "mute", "Mute", "闭嘴")
    )
    GlobalManager.moduleManager.registerModule(groupMsgPollingModule)
    GlobalManager.moduleManager.registerModule(helpModule)
    GlobalManager.moduleManager.registerModule(banModule)

    GlobalManager.moduleManager.loadModule(groupMsgPollingModule.name)
    GlobalManager.moduleManager.loadModule(helpModule.name)
    GlobalManager.moduleManager.loadModule(banModule.name)
}