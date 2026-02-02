package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg
import top.r3944realms.ltdmanager.napcat.event.message.GetGroupMsgHistoryEvent
import top.r3944realms.ltdmanager.napcat.request.message.GetGroupMsgHistoryRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil

class GroupMessagePollingModule(
    moduleName: String,
    val targetGroupId: Long,
    private val pollIntervalMillis: Long = 5_000L,
    private val msgHistoryCheck: Int = 15,
) : BaseModule(Modules.GROUP_MESSAGE_POLLING, moduleName) {
    private var scope: CoroutineScope? = null

    // 用 Flow 存消息，其他模块可以订阅
    private val _messagesFlow = MutableSharedFlow<List<MsgHistorySpecificMsg>>(
        replay = 1, // 保留最近一份消息
        extraBufferCapacity = 1
    )
    val messagesFlow: SharedFlow<List<MsgHistorySpecificMsg>> = _messagesFlow.asSharedFlow()

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 启动消息轮询 (群: $targetGroupId)")
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            while (isActive && loaded) {
                try {
                    val event = getNapCatClientOrNull()?.send<GetGroupMsgHistoryEvent>(
                        GetGroupMsgHistoryRequest(
                            count = msgHistoryCheck,
                            groupId = ID.long(targetGroupId)
                        )
                    )

                    val messages = event?.data?.messages ?: emptyList()
                    LoggerUtil.logger.debug("[$name] 拉取到 ${messages.size} 条消息")
                    _messagesFlow.emit(messages)
                } catch (e: Exception) {
                    LoggerUtil.logger.error("[$name] 拉取消息失败", e)
                }
                delay(pollIntervalMillis)
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载，停止轮询")
        scope?.cancel() // 取消协程
    }

}