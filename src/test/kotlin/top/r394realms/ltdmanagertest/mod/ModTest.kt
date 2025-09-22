package top.r394realms.ltdmanagertest.mod

import kotlinx.coroutines.delay
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.GlobalManager.napCatClient
import top.r3944realms.ltdmanager.module.ModGroupHandlerModule
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest


fun main() = GlobalManager.runBlockingMain {
    val groupId:Long = 538751386
    val selfQQId = 3327379836
    val selfNickName = "闲趣老土豆"
    // 创建模块实例

    val modGroupHandlerModule = ModGroupHandlerModule(
        moduleName = "ModGroup",
        targetGroupId = 339340846,
        answers = listOf("戏鸢", "一只戏鸢", "折戏鸢", "LostInLinearPast", "lostinlinearpast"),
    )

    // 注册模块到全局模块管理器

    GlobalManager.moduleManager.registerModule(modGroupHandlerModule)

    // 加载模块
    GlobalManager.moduleManager.loadModule(modGroupHandlerModule.name)
}
