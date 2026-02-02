package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupIgnoredNotifiesEvent
import top.r3944realms.ltdmanager.napcat.event.group.GetGroupSystemMsgEvent
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupIgnoredNotifiesRequest
import top.r3944realms.ltdmanager.napcat.request.group.GetGroupSystemMsgRequest
import top.r3944realms.ltdmanager.napcat.request.group.SetGroupAddRequestRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil

class GroupRequestHandlerModule(
    moduleName: String,
    private val client: NapCatClient,
    private val targetGroupId: Long,
    private val pollIntervalMillis: Long = 30_000L,
) : BaseModule(Modules.GROUP_REQUEST_HANDLER, moduleName) {

    private var scope: CoroutineScope? = null


    override fun onLoad() {
        LoggerUtil.logger.info("模块[$name]已装载，目标群组: $targetGroupId，轮询间隔: ${pollIntervalMillis}ms")

        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        // 启动轮询协程
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 轮询协程启动")
            while (isActive && loaded) {
                try {
                    LoggerUtil.logger.debug("[$name] 开始轮询群组请求...")

                    // 获取正常请求
                    LoggerUtil.logger.debug("[$name] 获取正常群系统消息...")
                    val systemEvent: GetGroupSystemMsgEvent =
                        client.send(GetGroupSystemMsgRequest())
                    LoggerUtil.logger.debug("[$name] 获取到 ${systemEvent.data.invitedRequest.size} 个邀请请求和 ${systemEvent.data.joinRequests.size} 个加群请求")

                    handleEvent(systemEvent)

                    // 获取被过滤的请求
                    LoggerUtil.logger.debug("[$name] 获取被过滤的群系统消息...")
                    val ignoredEvent: GetGroupIgnoredNotifiesEvent =
                        client.send(GetGroupIgnoredNotifiesRequest())
                    LoggerUtil.logger.debug("[$name] 获取到 ${ignoredEvent.data.invitedRequest.size} 个被过滤的邀请请求和 ${ignoredEvent.data.joinRequests.size} 个被过滤的加群请求")

                    handleEvent(ignoredEvent)

                    LoggerUtil.logger.debug("[$name] 本轮轮询完成，等待 ${pollIntervalMillis}ms 后继续")
                } catch (e: Exception) {
                    LoggerUtil.logger.error("[$name] 轮询执行异常", e)
                }
                delay(pollIntervalMillis)
            }
        }
    }


    public override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 已卸载")
        scope?.cancel()
    }

    private suspend fun handleEvent(event: Any) {
        if (!loaded) return
        LoggerUtil.logger.debug("[$name] 处理群请求事件: ${event.javaClass.simpleName}")

        val provider: GroupRequestProvider? = when (event) {
            is GetGroupSystemMsgEvent -> {
                LoggerUtil.logger.debug("[$name] 识别为正常群系统消息事件")
                event.asProvider()
            }
            is GetGroupIgnoredNotifiesEvent -> {
                LoggerUtil.logger.debug("[$name] 识别为被过滤群系统消息事件")
                event.asProvider()
            }
            else -> {
                LoggerUtil.logger.warn("[$name] 未知的事件类型: ${event.javaClass}")
                null
            }
        }

        provider?.getAllRequests()?.forEach { request ->
            if (!request.checked) {
                LoggerUtil.logger.info("[$name] 处理群请求: requestId=${request.requestId}, groupId=${request.groupId}, actor=${request.actor}, type=${request.javaClass}")
                if (request.groupId == targetGroupId) {
                    LoggerUtil.logger.info("[$name] 请求匹配目标群组 $targetGroupId，查询玩家状态...")
                    val status = queryPlayerStatus(request.invitorUin)
                    LoggerUtil.logger.info("[$name] 玩家 ${request.invitorUin} 状态查询结果: $status")

                    when (status) {
                        1 -> {
                            LoggerUtil.logger.info("[$name] 允许加群: groupId=${request.groupId}, invitorUin=${request.invitorUin}, requestId=${request.requestId}")
                            val setGroupAddRequestRequest = SetGroupAddRequestRequest(
                                true,
                                request.requestId.toString()
                            )
                            client.send<NapCatEvent>(setGroupAddRequestRequest)
                            LoggerUtil.logger.info("[$name] 已发送同意加群请求")
                        }

                        2, 3 -> {
                            val reason = if (status == 3) "审核未通过，或请使用填写白名单所用QQ号加群" else "白名单待审核，请通过后再加"
                            LoggerUtil.logger.info("[$name] 拒绝加群: groupId=${request.groupId}, invitorUin=${request.invitorUin}, status=$status, reason=$reason, requestId=${request.requestId}")
                            val request1 = SetGroupAddRequestRequest(
                                false,
                                request.requestId.toString(),
                                reason
                            )
                            client.send<NapCatEvent>(request1)
                            LoggerUtil.logger.info("[$name] 已发送拒绝加群请求")
                        }

                        else -> {
                            LoggerUtil.logger.warn("[$name] 未知玩家状态($status)，拒绝请求: invitorUin=${request.invitorUin}, requestId=${request.requestId}")
                            val request1 = SetGroupAddRequestRequest(
                                false,
                                request.requestId.toString(),
                                "未知状态"
                            )
                            client.send<NapCatEvent>(request1)
                            LoggerUtil.logger.info("[$name] 已发送拒绝加群请求（未知状态）")
                        }
                    }
                } else {
                    LoggerUtil.logger.debug("[$name] 请求群组 ${request.groupId} 不匹配目标群组 $targetGroupId，跳过处理")
                }
            }
        }

        LoggerUtil.logger.debug("[$name] 事件处理完成")
    }

    private fun queryPlayerStatus(actor: Long): Int {
        LoggerUtil.logger.debug("[$name] 查询玩家状态: qq=$actor")
        try {
            getConnection().use { conn ->
                val stmt = conn.prepareStatement(
                    "SELECT status FROM minecraft_manager_ltd_8.players WHERE qq=?"
                )
                stmt.setLong(1, actor)
                val rs = stmt.executeQuery()
                val status = if (rs.next()) rs.getInt("status") else 0
                LoggerUtil.logger.debug("[$name] 数据库查询结果: qq=$actor, status=$status")
                return status
            }
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 查询玩家状态失败: qq=$actor", e)
            return 0
        }
    }

    /**
     * 所有群系统请求的统一访问接口
     */
    interface GroupRequestProvider {
        fun getAllRequests(): List<GetGroupSystemMsgEvent.SystemInfo>
    }

    /**
     * 正常请求事件实现
     */
    private fun GetGroupSystemMsgEvent.asProvider(): GroupRequestProvider = object : GroupRequestProvider {
        override fun getAllRequests(): List<GetGroupSystemMsgEvent.SystemInfo> {
            return data.invitedRequest + data.joinRequests
        }
    }

    /**
     * 被过滤请求事件实现
     */
    private fun GetGroupIgnoredNotifiesEvent.asProvider(): GroupRequestProvider = object : GroupRequestProvider {
        override fun getAllRequests(): List<GetGroupSystemMsgEvent.SystemInfo> {
            return data.invitedRequest + data.joinRequests
        }
    }
    override fun info(): String = "模块: $name\n功能: 自动处理群组加群请求\n版本: 1.0"

    override fun help(): String = "本模块会轮询群组加群请求并根据数据库白名单自动同意或拒绝"
}