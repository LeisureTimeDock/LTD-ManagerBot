
package top.r3944realms.ltdmanager.napcat.event.system

import kotlinx.serialization.Serializable

/**
* BotExit事件
* @property data 响应数据
*/
@Serializable
class BotExitEvent: AbstractSystemEvent() {
    override fun subtype(): String {
        return "bot_exit"
    }
}
    