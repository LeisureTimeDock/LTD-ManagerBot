package top.r394realms.ltdmanagertest

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.module.GroupRequestHandlerModule


fun main() = GlobalManager.runBlockingMain {
    // 创建模块实例
    val groupModule = GroupRequestHandlerModule(
        client = GlobalManager.napCatClient,
        targetGroupId = 538751386
    )


    // 注册模块到全局模块管理器
    GlobalManager.moduleManager.registerModule(groupModule)

    // 加载模块
    GlobalManager.moduleManager.loadModule(groupModule.name)
}