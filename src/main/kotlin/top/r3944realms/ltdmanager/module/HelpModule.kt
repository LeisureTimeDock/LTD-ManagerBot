package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.module.common.CommandParser
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownManager
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownScope
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownStateProvider
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.CooldownFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.KeywordFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File

/**
 * HelpModule 提供全局模块帮助信息
 */
class HelpModule(
    moduleName: String,
    private val groupMessagePollingModule: GroupMessagePollingModule,
    private val selfId: Long,
    private val selfNickName: String,
    private val keywords: List<String> = listOf("help", "帮助"),
    private val cooldownMillis: Long = 30_000L
) : BaseModule("HelpModule", moduleName), PersistentState<HelpModule.HelpState> {

    // 命令解析器
    private val commandParser = CommandParser(keywords)
    private val MsgHistorySpecificMsg.textContent: String
        get() = message.joinToString("") { it.data.text ?: "" }

    // 持久化文件
    private val stateFile: File = getStateFileInternal("help_module_state.json", name)
    private val stateBackupFile: File = getStateFileInternal("help_module_state.json.bak", name)

    @Serializable
    data class HelpState(var lastTriggeredRealId: Long = -1L, var lastTriggerTime: Long = 0L)

    private var lastTriggerState: HelpState = loadState()

    // 冷却管理器
    private val cooldownManager by lazy {
        CooldownManager(
            cooldownMillis = cooldownMillis,
            scope = CooldownScope.Global,
            stateProvider = object : CooldownStateProvider<HelpState> {
                override fun load() = loadState()
                override fun save(state: HelpState) = saveState(state)
            },
            getLastTrigger = { state, _ -> state.lastTriggerTime to state.lastTriggeredRealId },
            updateTrigger = { state, _, realId, time -> state.copy(lastTriggeredRealId = realId, lastTriggerTime = time) },
            updateCooldownRealId = { state, _, realId -> state.copy(lastTriggeredRealId = realId) },
            groupId = groupMessagePollingModule.targetGroupId
        )
    }

    // 触发过滤器
    private val triggerFilter by lazy {
        TriggerMessageFilter(
            listOf(
                IgnoreSelfFilter(selfId),
                NewMessageFilter { _ -> lastTriggerState.lastTriggerTime to lastTriggerState.lastTriggeredRealId },
                KeywordFilter(keywords.toSet()),
                CooldownFilter(cooldownManager) { msg, remain -> sendCooldownMessage(napCatClient, msg.realId, remain) }
            )
        )
    }

    private var scope: CoroutineScope? = null

    override fun getStateFileInternal(): File = stateFile

    override fun getState(): HelpState = lastTriggerState

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已加载，监听 help 指令")
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            groupMessagePollingModule.messagesFlow.collect { messages ->
                if (loaded) handleMessages(messages)
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载，取消协程...")
        scope?.cancel()
        saveState(lastTriggerState)
        LoggerUtil.logger.info("[$name] 模块已卸载完成")
    }

    private suspend fun handleMessages(messages: List<MsgHistorySpecificMsg>) {
        val filtered = triggerFilter.filter(messages)
        val triggerMsg = filtered.maxByOrNull { it.time } ?: return

        val cmdPair = commandParser.parseCommand(triggerMsg.textContent)
        if (cmdPair != null) {
            val (_, arg) = cmdPair
            if (arg.isNotEmpty()) {
                val module = moduleMap[arg]
                if (module != null) sendModuleHelp(triggerMsg, arg, module)
                else sendText(triggerMsg, "未找到模块: $arg")
            } else {
                sendAllModulesHelp(triggerMsg)
            }
        }
    }

    private suspend fun sendAllModulesHelp(msg: MsgHistorySpecificMsg) {
        val messages = moduleMap.map { (name, module) ->
            val textBuilder = StringBuilder()
            textBuilder.appendLine("===== $name =====")
            textBuilder.appendLine(module.info())
            val helpText = module.help()
            if (helpText.isNotEmpty()) textBuilder.appendLine(helpText)
            textBuilder.appendLine().appendLine()
            SendForwardMsgRequest.Message(
                data = SendForwardMsgRequest.PurpleData(textBuilder.toString()),
                type = MessageType.Text
            )
        }

        val topMessage = SendForwardMsgRequest.TopForwardMsg(
            data = SendForwardMsgRequest.MessageData(
                content = messages,
                nickname = selfNickName,
                userId = ID.long(selfId)
            ),
            type = MessageType.Node
        )

        val request = SendForwardMsgRequest(
            groupId = ID.long(groupMessagePollingModule.targetGroupId),
            messages = listOf(topMessage),
            news = listOf(SendForwardMsgRequest.ForwardModelNews("点击查看所有模块信息")),
            prompt = "全局模块信息",
            source = "📚 HelpModule",
            summary = "信息，共 ${messages.size} 个模块"
        )

        napCatClient.sendUnit(request)
        updateTriggerState(msg)
    }

    private suspend fun sendModuleHelp(msg: MsgHistorySpecificMsg, moduleName: String, module: BaseModule) {
        val textBuilder = StringBuilder()
        textBuilder.appendLine("===== $moduleName =====")
        textBuilder.appendLine(module.info())
        val helpText = module.help()
        if (helpText.isNotEmpty()) textBuilder.appendLine(helpText)

        val message = SendForwardMsgRequest.Message(
            data = SendForwardMsgRequest.PurpleData(textBuilder.toString()),
            type = MessageType.Text
        )

        val topMessage = SendForwardMsgRequest.TopForwardMsg(
            data = SendForwardMsgRequest.MessageData(
                content = listOf(message),
                nickname = selfNickName,
                userId = ID.long(selfId)
            ),
            type = MessageType.Node
        )

        val request = SendForwardMsgRequest(
            groupId = ID.long(groupMessagePollingModule.targetGroupId),
            messages = listOf(topMessage),
            news = listOf(SendForwardMsgRequest.ForwardModelNews("点击查看模块 $moduleName 帮助")),
            prompt = "模块 $moduleName 帮助",
            source = "📚 HelpModule",
            summary = "模块 $moduleName 帮助信息"
        )

        napCatClient.sendUnit(request)
        updateTriggerState(msg)
    }

    private suspend fun sendText(msg: MsgHistorySpecificMsg, text: String) {
        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(msg.realId), text),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        napCatClient.sendUnit(request)
        updateTriggerState(msg)
    }

    private fun updateTriggerState(msg: MsgHistorySpecificMsg) {
        lastTriggerState.lastTriggeredRealId = msg.realId
        lastTriggerState.lastTriggerTime = msg.time
        saveState(lastTriggerState)
    }

    private suspend fun sendCooldownMessage(client: NapCatClient, realId: Long, remaining: Long) {
        val msg = "⏳ Help 查询过于频繁，请稍后再试（剩余 $remaining 秒）"
        LoggerUtil.logger.info("[$name] 发送冷却提示: $msg")
        client.sendUnit(
            SendGroupMsgRequest(
                MessageElement.reply(ID.long(realId), msg),
                ID.long(groupMessagePollingModule.targetGroupId)
            )
        )
    }

    // ---------------- 持久化 ----------------
    override fun saveState(state: HelpState) {
        try {
            if (stateFile.exists()) stateFile.copyTo(stateBackupFile, overwrite = true)
            stateFile.writeText(Json.encodeToString(state))
            LoggerUtil.logger.info("[$name] 已保存状态: lastTriggeredRealId=${state.lastTriggeredRealId}, lastTriggerTime=${state.lastTriggerTime}")
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 保存状态失败", e)
        }
    }

    override fun loadState(): HelpState {
        return try {
            val fileToRead = when {
                stateFile.exists() -> stateFile
                stateBackupFile.exists() -> stateBackupFile
                else -> null
            }
            if (fileToRead == null) return HelpState()
            Json.decodeFromString<HelpState>(fileToRead.readText())
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 读取状态失败，使用默认值", e)
            HelpState()
        }
    }

    override fun help(): String = "发送 'help' 获取所有模块帮助信息"

    override fun info(): String = "模块: $name\n功能: 提供全局模块帮助信息\n版本: 1.0"
}
