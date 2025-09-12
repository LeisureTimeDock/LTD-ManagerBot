package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent
import top.r3944realms.ltdmanager.napcat.event.account.GetStrangerInfoEvent
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupIgnoredNotifiesEvent
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupSystemMsgEvent
import top.r3944realms.ltdmanager.napcat.request.account.GetStrangerInfoRequest
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupIgnoredNotifiesRequest
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupSystemMsgRequest
import top.r3944realms.ltdmanager.napcat.request.group.SetGroupAddRequestRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 模块: 入群申请自动处理
 * 功能:
 *   1. 监听目标群的入群申请事件
 *   2. 根据 answers 列表自动同意或拒绝
 */
class ModGroupHandlerModule(
    moduleName: String,
    private val targetGroupId: Long,
    private val answers: List<String> = listOf("正确答案"),
    private val pollIntervalMillis: Long = 30_000L
) : BaseModule("ModGroupHandlerModule", moduleName), PersistentState<ModGroupHandlerModule.RejectRecords> {

    private var scope: CoroutineScope? = null
    private val stateFile: File = getStateFileInternal("reject_records.json", name)
    private val fileLock = ReentrantLock()
    private var stateCache: RejectRecords? = null
    private val json = Json { prettyPrint = true; encodeDefaults = true }

    @Serializable
    data class RejectRecord(
        val userId: Long,
        var reason: MutableList<String> = mutableListOf(),
        var rejectCount: Int = 0
    )

    /**
     * 记录所有被拒绝用户的Map，key = userId
     */
    @Serializable
    data class RejectRecords(
        val records: MutableMap<Long, RejectRecord> = mutableMapOf()
    )

    override fun getStateFileInternal(): File = stateFile

    override fun getState(): RejectRecords {
        if (stateCache == null) stateCache = loadState()
        return stateCache!!
    }

    override fun saveState(state: RejectRecords) {
        fileLock.withLock {
            try {
                stateFile.writeText(json.encodeToString(state))
            } catch (e: Exception) {
                LoggerUtil.logger.error("[$name] 保存拒绝记录失败", e)
            }
        }
    }

    override fun loadState(): RejectRecords {
        return try {
            if (!stateFile.exists()) return RejectRecords()
            val text = stateFile.readText()
            json.decodeFromString(RejectRecords.serializer(), text)
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 拒绝记录加载失败，使用默认值", e)
            RejectRecords()
        }
    }

    private fun addReject(userId: Long, reason: String) {
        val state = getState()
        val record = state.records[userId]
        if (record != null) {
            record.rejectCount += 1
            record.reason.add(reason)
        } else {
            state.records[userId] = RejectRecord(userId, mutableListOf(reason), 1)
        }
        saveState(state)
    }
    fun getRejectRecord(userId: Long): RejectRecord? {
        return getState().records[userId]
    }

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已装载，目标群组: $targetGroupId")
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 轮询协程启动")
            while (isActive && loaded) {
                try {
                    handleEvents()
                    delay(pollIntervalMillis)
                } catch (e: Exception) {
                    LoggerUtil.logger.error("[$name] 轮询异常", e)
                }
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载")
        scope?.cancel()
    }

    private suspend fun handleEvents() {
        val systemEvent: GetGroupSystemMsgEvent = napCatClient.send(GetGroupSystemMsgRequest())
        handleEvent(systemEvent)

        val ignoredEvent: GetGroupIgnoredNotifiesEvent = napCatClient.send(GetGroupIgnoredNotifiesRequest())
        handleEvent(ignoredEvent)
    }

    private suspend fun handleEvent(event: Any) {
        if (!loaded) return
        val provider: GroupRequestProvider? = when (event) {
            is GetGroupSystemMsgEvent -> event.asProvider()
            is GetGroupIgnoredNotifiesEvent -> event.asProvider()
            else -> null
        }

        provider?.getAllRequests()?.forEach { request ->
            if (!request.checked && request.groupId == targetGroupId) {
                LoggerUtil.logger.info("[$name] 处理请求: requestId=${request.requestId}, actor=${request.actor}")
                val answerAllow = answers.contains(request.message)
                if(answerAllow) {
                    val info = napCatClient.send<GetStrangerInfoEvent>(GetStrangerInfoRequest(ID.long(request.requestId)))
                    val levelAllow = info.data.qqLevel >= 16
                    val setRequest = SetGroupAddRequestRequest(
                        levelAllow,
                        request.requestId.toString(),
                        if(!levelAllow) "QQ等级低于16级" else ""
                    )
                    napCatClient.send<NapCatEvent>(setRequest)
                    if (levelAllow) napCatClient.send<NapCatEvent>(SendGroupMsgRequest(listOf(MessageElement.text(formatRejectRecordMessage(request.requestId))), ID.long(targetGroupId)))
                    LoggerUtil.logger.info("[$name] 已${if (levelAllow) "同意" else "拒绝"}请求${if(!levelAllow) ",等级不够,${info.data.qqLevel}" else "" }: ${request.requestId}")
                } else {
                    napCatClient.sendUnit(SetGroupAddRequestRequest(false, request.requestId.toString(), "答案错误,拒绝次数：${getRejectRecord(request.requestId)?.rejectCount}"))
                    addReject(request.actor, "答案错误:${request.message}")
                    LoggerUtil.logger.info("[$name] 答案错误：${request.message}，已拒绝请求: ${request.requestId}")
                }

            }
        }
    }
    fun formatRejectRecordMessage(userId: Long): String {
        val record = getRejectRecord(userId)
        return if (record != null) {
            """
        用户QQ号：${record.userId}
        尝试次数：${record.rejectCount}
        最终评分：${rate(record.rejectCount)} 
        尝试答案：【${record.reason.joinToString("，")}】
        """.trimIndent()
        } else {
            """
        用户QQ号：${userId}
        尝试次数：0
        最终评分：SSS  
        """.trimIndent()
        }
    }
    private fun rate(count: Int): String = when (count) {
        0 -> "S"
        1 -> "A"
        2 -> "B"
        3 -> "C"
        4 -> "D"
        else -> "F"
    }
    interface GroupRequestProvider {
        fun getAllRequests(): List<GetGroupSystemMsgEvent.SystemInfo>
    }

    private fun GetGroupSystemMsgEvent.asProvider(): GroupRequestProvider = object : GroupRequestProvider {
        override fun getAllRequests(): List<GetGroupSystemMsgEvent.SystemInfo> =
            data.invitedRequest + data.joinRequests
    }

    private fun GetGroupIgnoredNotifiesEvent.asProvider(): GroupRequestProvider = object : GroupRequestProvider {
        override fun getAllRequests(): List<GetGroupSystemMsgEvent.SystemInfo> =
            data.invitedRequest + data.joinRequests
    }

    override fun info(): String = """
        模块: $name
        功能: 自动处理指定群组的入群申请
              1. 根据答案列表自动同意或拒绝
              2. 拒绝记录会保存到本地，并可查询尝试次数和尝试答案
              3. 用户通过验证且等级满足要求时，会向群里发送消息，显示用户QQ号、尝试次数、评分和尝试答案
        版本: 1.0
        """.trimIndent()
    override fun help(): String = "轮询群组入群申请，根据答案列表自动同意或拒绝，并记录拒绝用户信息"
}