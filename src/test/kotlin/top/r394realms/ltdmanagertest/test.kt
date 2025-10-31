package top.r394realms.ltdmanagertest

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.module.GroupRequestHandlerModule
import top.r3944realms.ltdmanager.module.StateModule


fun main() = GlobalManager.runBlockingMain {
    // 创建模块实例
    val stateModule = StateModule(
        moduleName = "Globe",
        onlineName = "[\uD83D\uDFE2] 闲趣老土豆🥔",
        offlineName = "[\uD83D\uDD34] 闲趣老土豆🥔"
    )


    // 注册模块到全局模块管理器
    GlobalManager.moduleManager.registerModule(stateModule)

    // 加载模块
    GlobalManager.moduleManager.loadModule(stateModule.name)
}