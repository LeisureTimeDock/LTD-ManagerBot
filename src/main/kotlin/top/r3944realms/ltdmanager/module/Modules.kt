package top.r3944realms.ltdmanager.module

import java.util.*

object Modules {
    private val MODULES: MutableList<String> = LinkedList();
    val BAN: String = register("BanModule")
    val APPLY_WHITELIST: String = register("ApplyWhitelistModule")
    val DG_LAB: String = register("DGLabModule")
    val GROUP_MESSAGE_POLLING: String = register("GroupMessagePollingModule")
    val GROUP_REQUEST_HANDLER: String = register("GroupRequestHandlerModule")
    val HELP: String = register("HelpModule")
    val MAIL: String = register("MailModule")
    val MC_SERVER_STATUS: String = register("MCServerStatusModule")
    val MOD_GROUP_HANDLER: String = register("ModGroupHandlerModule")
    val RCON_PLAYER_LIST: String = register("RconPlayerListModule")
    val INVITATION_CODE: String = register("InvitationCodeModule")
    val STATE: String = register("StateModule")
    fun register(name: String): String {
        MODULES.add(name)
        return name
    }
    fun getModules(): Array<String> {
        return MODULES.toTypedArray();
    }
}