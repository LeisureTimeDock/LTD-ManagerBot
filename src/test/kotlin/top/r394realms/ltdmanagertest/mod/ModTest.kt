package top.r394realms.ltdmanagertest.mod

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.module.ModGroupHandlerModule


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

    GlobalManager.moduleManager.register(modGroupHandlerModule)

    // 加载模块
    GlobalManager.moduleManager.load(modGroupHandlerModule.name)
}
