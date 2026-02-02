package top.r3944realms.ltdmanager.module

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.r3944realms.dg_lab.api.message.IPowerBoxMsg
import com.r3944realms.dg_lab.api.message.argType.ChangePolicy
import com.r3944realms.dg_lab.api.message.argType.Channel
import com.r3944realms.dg_lab.api.websocket.message.MessageDirection
import com.r3944realms.dg_lab.manager.DGPBClientManager
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.dglab.DgLab
import top.r3944realms.ltdmanager.dglab.model.game.GameClientOperation
import top.r3944realms.ltdmanager.dglab.model.game.GameServerOperation
import top.r3944realms.ltdmanager.dglab.model.game.Player
import top.r3944realms.ltdmanager.dglab.model.pulseware.DefaultPulseData
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.KeywordFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupMemberListEvent
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupMemberListRequest
import top.r3944realms.ltdmanager.napcat.request.message.SetMsgEmojiLikeRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import kotlin.math.abs

/**
 * 数据 {QQ}
 */
class DGLabModule(
    moduleName: String,
    private val groupMessagePollingModule : GroupMessagePollingModule,
    private val selfId: Long,
    val adminIds: List<Long> = listOf(),
    val maxClientNumber: Int = 10,
    val commandHead: List<String> = listOf("dglab"),
) : BaseModule(Modules.DG_LAB, moduleName), PersistentState<DGLabModule.DgLabState> {

    var dgLabManager: DgLab? = null
    private var scope: CoroutineScope? = null
    private var dglabCommandDispatcher: CommandDispatcher<Player> = CommandDispatcher<Player>().apply {
        for (command in commandHead) register(
            literal<Player>(command)
                .then(literal<Player?>("server").requires { adminIds.contains(it.id) }
                    .then(literal<Player?>("start").executes { startDgLab() })
                    .then(literal<Player?>("stop").executes { stopDgLab() })
                    .then(literal<Player?>("stopAllClient").executes { stopAllDgLabClient() })
                )
                .then(literal<Player?>("client")
                    .then(literal<Player?>("start").executes { startClient(it.source.id) })
                    .then(literal<Player?>("stop").executes { stopClient(it.source.id) })
                )
                .then(literal<Player?>("strength")
                    .then(argument<Player?, String>("channel", StringArgumentType.string())
                        .then(literal<Player?>("add")
                            .then(argument<Player?, Int>("value", IntegerArgumentType.integer(-200, 200))
                                .executes { strengthAdd(it.source.id, StringArgumentType.getString(it, "channel"), IntegerArgumentType.getInteger(it, "value")) }
                            )
                        )
                        .then(literal<Player?>("set")
                            .then(argument<Player?, Int>("value", IntegerArgumentType.integer(0, 200))
                                .executes { strengthSet(it.source.id, StringArgumentType.getString(it, "channel"), IntegerArgumentType.getInteger(it, "value")) }
                            )
                        )
                    )
                    .then(argument<Player?, Long>("player", LongArgumentType.longArg())
                        .then(argument<Player?, String>("channel", StringArgumentType.string())
                            .then(literal<Player?>("add")
                                .then(argument<Player?, Int>("value", IntegerArgumentType.integer(-200, 200))
                                    .executes { strengthAdd(LongArgumentType.getLong(it, "player"), StringArgumentType.getString(it, "channel"), IntegerArgumentType.getInteger(it, "value")) }
                                )
                            )
                            .then(literal<Player?>("set")
                                .then(argument<Player?, Int>("value", IntegerArgumentType.integer(0, 200))
                                    .executes { strengthSet(LongArgumentType.getLong(it, "player"), StringArgumentType.getString(it, "channel"), IntegerArgumentType.getInteger(it, "value")) }
                                )
                            )
                        )
                    )
                )
                .then(literal<Player?>("pulse")
                    .then(argument<Player?, String>("channel", StringArgumentType.string())
                        .then(literal<Player?>("clear").executes { pulseClear(it.source.id, StringArgumentType.getString(it, "channel")) })
                        .then(literal<Player?>("set")
                            .then(argument<Player?, String>("pulseName", StringArgumentType.string())
                                .then(argument<Player?, Int>("timer", IntegerArgumentType.integer(0, Int.MAX_VALUE))
                                    .executes { pulseSet(it.source.id, StringArgumentType.getString(it, "channel"), StringArgumentType.getString(it, "pulseName"), IntegerArgumentType.getInteger(it, "timer")) }
                                )
                            )
                        )
                    )
                    .then(argument<Player?, Long>("player", LongArgumentType.longArg())
                        .then(argument<Player?, String>("channel", StringArgumentType.string())
                            .then(literal<Player?>("clear").executes { pulseClear(LongArgumentType.getLong(it, "player"), StringArgumentType.getString(it, "channel")) })
                            .then(literal<Player?>("set")
                                .then(argument<Player?, String>("pulseName", StringArgumentType.string())
                                    .then(argument<Player?, Int>("timer", IntegerArgumentType.integer(0, Int.MAX_VALUE))
                                        .executes { pulseSet(LongArgumentType.getLong(it, "player"), StringArgumentType.getString(it, "channel"), StringArgumentType.getString(it, "pulseName"), IntegerArgumentType.getInteger(it, "timer")) }
                                    )
                                )
                            )
                        )
                    )
                )
        )
//                .then(literal<Player?>("info").executes {}
//                    .then(argument<Player?, String>("player", StringArgumentType.string()).executes {})
//                )

    }
    private val stateFile: File = getStateFileInternal("dg_lab_state.json", name)
    private val stateBackupFile: File = getStateFileInternal("dg_lab_state.json.bak", name)
    private var dgLabState = loadState()
    override fun getState(): DgLabState = dgLabState
    override fun getStateFileInternal(): File = stateFile

    private val triggerFilter by lazy {
        TriggerMessageFilter(
            listOf(
                IgnoreSelfFilter(selfId),
                NewMessageFilter { userId ->
                    dgLabState.getLastTriggerTime(userId) to dgLabState.getLastTriggerRealId(userId)
                },
                KeywordFilter(commandHead.toSet())
            )
        )
    }

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已装载，监听群组: ${groupMessagePollingModule.targetGroupId}")
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 轮询协程启动")
            dgLabManager = DgLab()
            val gameServerOperation = GameServerOperation(napCatClient, groupMessagePollingModule.targetGroupId)
            dgLabManager?.createServerManager(gameServerOperation)?.let { dgLabManager?.initServerManager(it) }
            gameServerOperation.serverManager = dgLabManager?.serverManager
            init()
            groupMessagePollingModule.messagesFlow.collect { messages ->
                if (loaded) handleMessages(messages)
            }
        }
    }
    override suspend fun onUnload() {
        saveState(dgLabState)
        dgLabManager?.close()
        scope?.cancel()
        LoggerUtil.logger.info("[$name] 模块已卸载完成")
    }

    private suspend fun handleMessages(messages: List<MsgHistorySpecificMsg>) {
        if (messages.isEmpty()) return

        // 先对所有消息进行 @ 提及处理
        val processedMessages = messages.map { msg ->
            val processedText = processMessageMentionsToLong(msg)
            msg to processedText
        }

        val triggerMsgs = processedMessages
            .filter { (msg, _) -> filterTriggerMessages(listOf(msg)).isNotEmpty() }
            .map { (msg, processedText) -> Triple(msg, msg.userId, processedText) }

        if (triggerMsgs.isEmpty()) return

        var refPlayer: Player? = null
        var refMsg: MsgHistorySpecificMsg? = null
        try {
            triggerMsgs.forEach { (msg, userId, processedText) ->
                refMsg = msg
                LoggerUtil.logger.info("[$name] 原始消息用户: $userId")
                LoggerUtil.logger.info("[$name] 处理后的命令: $processedText")

                refPlayer = dgLabManager?.getPlayerManager()?.getPlayer(userId)
                dgLabState = dgLabState.updateOrCreate(userId, msg.realId, msg.time)
                val execute = dglabCommandDispatcher.execute(processedText, refPlayer)
                scope?.launch {
                    GlobalManager.napCatClient.sendUnit(
                        SetMsgEmojiLikeRequest(
                            if (execute == 0) 1.0 else 2.0, ID.long(msg.realId), true
                        )
                    )
                }
            }
        } catch (e: CommandSyntaxException) {
            val reader = e.input // 用户输入
            val cursor = e.cursor
            val partialInput = reader.substring(0, cursor)
            if (refPlayer != null) {
                val node = dglabCommandDispatcher.parse(
                    partialInput,
                    dgLabManager?.getPlayerManager()?.getPlayer(refPlayer!!.id)
                ).context.nodes.lastOrNull()?.node
                val usage = if (node != null) {
                    val values = dglabCommandDispatcher.getSmartUsage(node, refPlayer).values
                    if(!values.isEmpty()) "目前节点可使用的子命令: $values"
                    else "目前节点无用法"

                } else {
                    "未找到用法"
                }

                sendFailedMessage(
                    napCatClient,
                    text = "指令解析错误:\n ${e.message}\n\n$usage",
                    qq = refMsg?.userId,
                    realId = refMsg?.realId,
                    time = refMsg?.time
                )
            }
        }
        catch (e: Exception) {
            sendFailedMessage(napCatClient, text = "系统错误，请联系管理员: ${e.message}")
        } finally {
            saveState(dgLabState)
        }
    }
    /**
     * 处理整个消息中的 @ 提及，转换为 Long 类型，并清理多余空格
     */
    private fun processMessageMentionsToLong(msg: MsgHistorySpecificMsg): String {
        val processedText = msg.message.joinToString(" ") { seg ->
            when (seg.type) {
                MessageType.At -> {
                    // 处理 @ 提及，转换为 Long
                    seg.data.qq?.let { qq ->
                        when (qq) {
                            is ID.StringValue -> qq.value.toLong().toString()
                            is ID.LongValue -> qq.value.toString()
                        }
                    } ?: seg.data.text ?: ""
                }
                MessageType.Text -> {
                    seg.data.text ?: ""
                }
                else -> ""
            }
        }.trim()

        // 清理多余空格：将多个连续空格替换为单个空格
        return processedText.replace(Regex("\\s+"), " ")
    }
    private suspend fun filterTriggerMessages(
        messages: List<MsgHistorySpecificMsg>
    ): List<MsgHistorySpecificMsg> = triggerFilter.filter(messages)
    private suspend fun init() {
        val getGroupMemberListEvent = napCatClient.send<GetGroupMemberListEvent>(
            GetGroupMemberListRequest(
                ID.long(groupMessagePollingModule.targetGroupId),
                false
            )
        )
        dgLabManager?.initOrLoadPlayerManager(getGroupMemberListEvent.data.filter { !it.isRobot }
            .associate { it.userId to it.nickname })
        dgLabManager?.initClientManager()
    }
//    private fun getHelp(): Int {
//        scope?.launch {
//            sendMessage()
//        }
//        return 1
//    }
    private fun startDgLab(): Int {
        dgLabManager?.getServer()?.start()
        return 1
    }
    private fun stopDgLab(): Int {
        dgLabManager?.getServer()?.stop()
        return 1
    }
    private fun stopAllDgLabClient(): Int {
        dgLabManager?.clientManager?.stopAll()
        return 1
    }
    private fun startClient(qq: Long): Int {
        if (dgLabManager?.getPlayerManager()?.getOnlinePlayerSize()!! > maxClientNumber) {
            scope!!.launch {
                sendFailedMessage(napCatClient, text = "无法启动新的客户端, 因为已到达最大连接数${maxClientNumber}")
            }
            return -1
        }
        val operation = GameClientOperation(
            napCatClient,
            groupMessagePollingModule.targetGroupId,
            dgLabManager!!.getPlayerManager(),
            qq
        )
        val dgpbClientManager = dgLabManager?.getClientOrCreate(
            qq.toString(),
            operation
        )
        operation.clientSelf = dgpbClientManager
        dgpbClientManager?.start()

        return 1
    }
    private fun stopClient(qq: Long): Int {
        dgLabManager?.getClient(qq.toString())?.stop()
        return 1
    }
    private fun strengthAdd(qq: Long, channel: String, value: Int): Int {
        val client = dgLabManager?.getClient(qq.toString()) ?: return -1
        val changePolicy = if(value >= 0) ChangePolicy.INCREASE else ChangePolicy.DECREASE
        val strengthValue = abs(value)

        when(channel) {
            "a" -> sendStrengthChange(client, Channel.A, changePolicy, strengthValue)
            "b" -> sendStrengthChange(client, Channel.B, changePolicy, strengthValue)
            "ab" -> {
                sendStrengthChange(client, Channel.A, changePolicy, strengthValue)
                sendStrengthChange(client, Channel.B, changePolicy, strengthValue)
            }
        }
        return 0
    }
    private fun strengthSet(qq: Long, channel: String, value: Int): Int {
        val client = dgLabManager?.getClient(qq.toString()) ?: return -1
        when(channel) {
            "a" -> sendStrengthChange(client, Channel.A, ChangePolicy.GOTO, value)
            "b" -> sendStrengthChange(client, Channel.B, ChangePolicy.GOTO, value)
            "ab" -> {
                sendStrengthChange(client, Channel.A, ChangePolicy.GOTO, value)
                sendStrengthChange(client, Channel.B, ChangePolicy.GOTO, value)
            }
        }
        return 0
    }

    private fun sendStrengthChange(client: DGPBClientManager, channel: Channel, policy: ChangePolicy, value: Int) {
        client.send(IPowerBoxMsg.StrengthChange(channel, policy, value)
            .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
    }

    private fun pulseClear(qq: Long, channel: String): Int {
        val client = dgLabManager?.getClient(qq.toString()) ?: return -1
        when(channel) {
            "a" -> client.send(IPowerBoxMsg.Clear(Channel.A)
                .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
            "b" -> client.send(IPowerBoxMsg.Clear(Channel.B)
                .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
            "ab" -> {
                client.send(IPowerBoxMsg.Clear(Channel.A)
                    .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
                client.send(IPowerBoxMsg.Clear(Channel.B)
                    .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
            }
        }
        return 0
    }
    private fun pulseSet(qq: Long, channel: String, pulseName: String, timer: Int): Int {
        val client = dgLabManager?.getClient(qq.toString()) ?: return -1
        val pulse = DefaultPulseData.allPulseWaveLists()[pulseName] ?: return -2
        when(channel) {
            "a" -> client.send(IPowerBoxMsg.Pulse(Channel.A, pulse, timer)
                .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
            "b" -> client.send(IPowerBoxMsg.Pulse(Channel.B, pulse, timer)
                .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
            "ab" -> {
                client.send(IPowerBoxMsg.Pulse(Channel.A, pulse, timer)
                    .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
                client.send(IPowerBoxMsg.Pulse(Channel.B, pulse, timer)
                    .toPowerBoxMessage(client.sharedData.connectionId, client.sharedData.targetWSId, MessageDirection.DirectType.CLIENT_TO_APPLICATION))
            }
        }
        return 0
    }

    private suspend fun sendMessage(
        client: NapCatClient,
        qq: Long,
        realId: Long,
        time: Long,
        text: String = "正常消息"
    ) {
        LoggerUtil.logger.info("[$name] 发送消息: realId=$realId, text=$text")

        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(realId), text),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        client.sendUnit(request)
        LoggerUtil.logger.info("[$name] 已发送 消息")

        // 更新触发的最大 realId
        dgLabState = dgLabState.updateOrCreate(qq, realId, time)
    }

    private suspend fun sendFailedMessage(
        client: NapCatClient,
        qq: Long? = null,
        realId: Long? = null,
        time: Long? = null,
        text: String = "失败消息"
    ) {
        LoggerUtil.logger.info("[$name] 发送失败消息: realId=$realId, text=$text")
        if (realId != null && qq != null && time != null) {
            val request = SendGroupMsgRequest(
                MessageElement.reply(ID.long(realId), text),
                ID.long(groupMessagePollingModule.targetGroupId)
            )
            client.sendUnit(request)
            LoggerUtil.logger.info("[$name] 已发送 失败消息")

            // 更新触发的最大 realId
            dgLabState = dgLabState.updateOrCreate(qq, realId, time)
        } else {
            val request = SendGroupMsgRequest(
                listOf(MessageElement.text(text)),
                ID.long(groupMessagePollingModule.targetGroupId)
            )
            client.sendUnit(request)
            LoggerUtil.logger.info("[$name] 已发送 失败消息[无指定对象]")
        }
    }
    // -------- 持久化 -----------
    @Serializable
    data class DgLabDetail(
        val realId : Long,
        val time: Long,
    )


    @Serializable
    data class DgLabState(
        val map: Map<Long, DgLabDetail> = emptyMap()
    ) {
        fun getLastTriggerTime(userId: Long): Long = map[userId]?.time ?: -1
        fun getLastTriggerRealId(userId: Long): Long = map[userId]?.realId ?: -1

        /**
         * 更新或创建某个用户的触发信息
         * - 如果传了 realId，则更新 realId
         * - 如果传了 time，则更新 time
         * - 其他字段保持原值
         */
        fun updateOrCreate(
            userId: Long,
            realId: Long? = null,
            time: Long? = null
        ): DgLabState {
            val old = map[userId]
            val newDetail = DgLabDetail(
                realId = realId ?: old?.realId ?: -1,
                time = time ?: old?.time ?: -1
            )
            val newMap = map.toMutableMap().apply { put(userId, newDetail) }
            return copy(map = newMap)
        }
    }

    override fun saveState(state: DgLabState) {
        try {
            if (stateFile.exists()) stateFile.copyTo(stateBackupFile, overwrite = true)
            stateFile.writeText(Json.encodeToString(state))
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 保存状态失败", e)
        }
    }

    override fun loadState(): DgLabState {
        return try {
            val fileToRead = when {
                stateFile.exists() -> stateFile
                stateBackupFile.exists() -> stateBackupFile
                else -> null
            } ?: return DgLabState()

            Json.decodeFromString<DgLabState>(fileToRead.readText())
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 读取状态失败", e)
            DgLabState()
        }
    }
}