package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.blessingskin.request.invitecode.GenerateInvitationCodeRequest
import top.r3944realms.ltdmanager.blessingskin.response.ResponseResult
import top.r3944realms.ltdmanager.blessingskin.response.invitecode.InvitationCodeGenerationResponse
import top.r3944realms.ltdmanager.core.mail.mail
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownManager
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownScope
import top.r3944realms.ltdmanager.module.common.cooldown.CooldownStateProvider
import top.r3944realms.ltdmanager.module.common.filter.TriggerMessageFilter
import top.r3944realms.ltdmanager.module.common.filter.type.CooldownFilter
import top.r3944realms.ltdmanager.module.common.filter.type.IgnoreSelfFilter
import top.r3944realms.ltdmanager.module.common.filter.type.KeywordFilter
import top.r3944realms.ltdmanager.module.common.filter.type.NewMessageFilter
import top.r3944realms.ltdmanager.module.exception.InvitationCodeException
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.event.message.GetFriendMsgHistoryEvent
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.utils.HtmlTemplateUtil
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


/*
    1. 订阅消息模块 (触发关键词, 注意过滤自己的消息，避免重复触发) [Done]
    2. 根据QQ号去查询机器人数据库中的视图表的id (此操作耗时，应设置针对指定用户的持久化冷却)
    3. id存在 [错误处理: id不存在提醒用户为无法查询到你的id，请联系管理员检查状态]
        i. effective 和 is_used 均为 1，
            则回复提醒你已经使用了你的邀请码，切勿重复发送

        ii. effective 为 1 且 is_used 为 0
            则查询token_id对应的token记录然后构造发送邮件
            提醒用户邮件已发送

        iii. effective 为 0
            则先通过API创建Token 获取来的响应 [错误处理: 当获取的json消息解析中success为false，则回复用户message中的错误信息]
            用Token去邀请码数据库中查询token_id,将其记录在机器人数据库对应白名单id映射token_id表中 [存在则更新，不存在则插入],
            然后按ii.执行

 */
/*
api格式 https://skins.r3944realms.top/api/invitation-codes/generate?token=XXXX&amount=1
成功消息：
{
    "success": true,
    "message": "邀请码生成成功",
    "data": [
        {
            "code": "XXXXXXX",
            "generated_at": "2025-08-29T09:36:36.910623Z",
            "expires_at": "2025-09-05T09:36:36.910506Z"
        }
    ]
}
失败消息：
{
    "success": false,
    "message": "无效的 API Token"
}
*/

class InvitationCodesModule(
    moduleName: String,
    private val groupMessagePollingModule: GroupMessagePollingModule,
    private val mailModule: MailModule,
    private val apiToken: String,
    selfId: Long,
    private val cooldownMillis: Long = 120_000,
    private val keywords: Set<String> = setOf("申请邀请码")
) : BaseModule("InvitationCodesModule", moduleName), PersistentState<InvitationCodesModule.LastTriggerMapState> {

    private var scope: CoroutineScope? = null
    private val stateFile: File = getStateFileInternal("invitation_codes_quarry_state.json", name)
    private val stateBackupFile: File = getStateFileInternal("invitation_codes_quarry_state.json.bak", name)
    private val cooldownManager by lazy{ CooldownManager(
            cooldownMillis = cooldownMillis,
            scope = CooldownScope.PerUser,
            stateProvider = object : CooldownStateProvider<LastTriggerMapState> {
                override fun load() = loadState()
                override fun save(state: LastTriggerMapState) = saveState(state)
            },
            getLastTrigger = { state, qq ->
                val detail = state.map[qq]
                (detail?.time ?: -1L) to (detail?.lastCooldownRealId ?: -1L)
            },
            updateTrigger = { state, qq, realId, time ->
                val id = requireNotNull(qq)
                state.updateLastTrigger(id, realId, time)
            },
            updateCooldownRealId = { state, qq, realId ->
                val id = requireNotNull(qq)
                state.updateLastCooldownRealId(id, realId)
            },
            groupId = groupMessagePollingModule.targetGroupId
        )
    }
    // 在 InvitationCodesModule 类里添加：
    private val triggerFilter = TriggerMessageFilter(
        listOf(
            IgnoreSelfFilter(selfId),
            NewMessageFilter { qq ->
                lastTriggerMapState.getLastTriggerTime(qq) to lastTriggerMapState.getLastTriggerRealId(qq)
            },
            KeywordFilter(keywords),
            CooldownFilter(
                cooldownManager = cooldownManager,
                sendCooldown = { msg, remain ->
                    sendCooldownMessage(
                        napCatClient,
                        msg.userId,
                        msg.realId,
                        "⏳ 申请邀请码过于频繁（剩余 $remain 秒后自动申请）"
                    )
                }
            )
        )
    )


    private val fileLock = ReentrantLock()

    private var lastTriggerMapState = loadState()
    override fun getStateFileInternal(): File = stateFile
    override fun getState(): LastTriggerMapState = lastTriggerMapState
    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块已装载，目标群组: ${groupMessagePollingModule.targetGroupId}")
        LoggerUtil.logger.info("[$name] 上次触发状态: lastTriggerMap=${lastTriggerMapState.map}")
        LoggerUtil.logger.info("[$name] 关键词列表: $keywords")

        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            LoggerUtil.logger.info("[$name] 轮询协程启动")
            groupMessagePollingModule.messagesFlow.collect { messages ->
                if (loaded) handleMessages(messages)
            }
        }

        // 定时落盘（防止异常退出丢状态）
        scope!!.launch {
            while (isActive) {
                delay(60_000) // 每分钟保存一次
                saveState(lastTriggerMapState)
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载，保存状态...")
        saveState(lastTriggerMapState)
        LoggerUtil.logger.info("[$name] 模块卸载，取消协程...")
        scope?.cancel()
        LoggerUtil.logger.info("[$name] 模块已卸载完成")
    }

    // =========================
    // 消息处理主流程
    // =========================
    private suspend fun handleMessages(messages: List<GetFriendMsgHistoryEvent.SpecificMsg>) {
        if (messages.isEmpty()) return
        val triggerMsgs = filterTriggerMessages(messages)
        if (triggerMsgs.isEmpty()) return

        try {
            val hadValidCodeButNotUsed = mutableListOf<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>()
            val needNewCode = mutableListOf<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>()

            getIdAndSelectSituation(triggerMsgs, hadValidCodeButNotUsed, needNewCode)
            createAndSearchInvitationCodeIdsThenUpdateDate(needNewCode)
            hadVaildCodeButNotUseListHandler(hadValidCodeButNotUsed + needNewCode)
        } catch (e: Exception) {
            sendFailedMessage(napCatClient, text = "系统错误，请联系管理员: $e")
        } finally {
            saveState(lastTriggerMapState)
        }
    }

    /** 过滤出符合条件的触发消息 */
    private suspend fun filterTriggerMessages(
        messages: List<GetFriendMsgHistoryEvent.SpecificMsg>
    ): List<GetFriendMsgHistoryEvent.SpecificMsg> {

        // 先应用通用过滤器
        val filtered = triggerFilter.filter(messages)

        // 再做 groupBy -> 只保留每个用户最新一条
        return filtered
            .groupBy { it.userId }
            .mapNotNull { (_, msgs) -> msgs.maxByOrNull { it.time } }
    }

    private suspend fun getIdAndSelectSituation(msgs: List<GetFriendMsgHistoryEvent.SpecificMsg>,
                                                hadVaildCodeButNotUseList : MutableList<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>,
                                                needNewCodeList: MutableList<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>) {
        if (msgs.isEmpty()) return

        val qqIds = msgs.map { it.userId }
        val placeholders = java.lang.String.join(",", Collections.nCopies(qqIds.size, "?"))
        // 修正SQL语句的表名引用
        val sql = """
        SELECT q.player_id, q.effective, q.is_used, q.qq 
        FROM ltd_manager_bot.qualified_user_info q 
        WHERE q.qq IN ($placeholders)
        """.trimIndent()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    // 设置所有参数
                    for (i in qqIds.indices) {
                        pstmt.setLong(i + 1, qqIds[i])
                    }

                    pstmt.executeQuery().use { rs ->
                        // 创建结果映射表
                        val resultMap = mutableMapOf<Long, Triple<Long?, Boolean?, Boolean?>>()

                        while (rs.next()) {
                            val qq = rs.getLong("qq")
                            val playerId = rs.getLong("player_id")
                            // 处理可能的null值
                            val playerIdValue = if (rs.wasNull()) null else playerId
                            val effective = rs.getBoolean("effective")
                            val isUsed = rs.getBoolean("is_used")

                            resultMap[qq] = Triple(playerIdValue, effective, isUsed)
                        }

                        // 分类处理每个消息
                        for (msg in msgs) {
                            val result = resultMap[msg.userId]

                            when {
                                result == null -> {
                                    // 数据库中没有记录, 属于是异常
                                    LoggerUtil.logger.error("[$name] 无法查询该QQ号为:${msg.userId}的白名单ID，可能该用户非白名单成员")
                                    sendFailedMessage(napCatClient, msg.userId, msg.realId, msg.time, "无法查询到你的白名单应用id，请联系管理员检查状态，对应QQ号：${msg.userId}")
                                }
                                result.first != null && result.second == true && result.third == true -> {
                                    // 有player_id且已使用
                                    LoggerUtil.logger.info("[$name] 该QQ号为:${msg.userId}的白名单ID是${result.first}，已使用对应激活码")
                                    sendMessage(napCatClient, msg.userId, msg.realId, msg.time, "你已经使用了你的邀请码，切勿重复发送")
                                }
                                result.first != null && result.second == true && result.third == false -> {
                                    // 有player_id、有效且未使用
                                    LoggerUtil.logger.info("[$name] 该QQ号为:${msg.userId}的白名单ID是${result.first}，已有对应激活码但未使用")
                                    hadVaildCodeButNotUseList.add(result.first!! to msg)
                                }
                                result.first != null && result.second == false -> {
                                    // 没有player_id但有效，需要新code或处理
                                    needNewCodeList.add(result.first!! to msg)
                                }
                                else -> {
                                    //其它情况，异常，不应该出现
                                    sendFailedMessage(napCatClient, msg.userId, msg.realId, msg.time, "非法状态，请联系管理员：$result")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // 更好的错误处理
            LoggerUtil.logger.error("[$name] 批量查询用户资格信息失败: ${e.message}", e)
            sendFailedMessage(napCatClient, text = "批量查询用户资格信息失败，请联系管理员: ${e.message}")
        }
    }
    private suspend fun hadVaildCodeButNotUseListHandler(list: List<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>) {
        if (list.isEmpty()) return

        val whiteListIds = list.map { it.first }
        val placeholders = java.lang.String.join(",", Collections.nCopies(whiteListIds.size, "?"))

        val sql = """
                SELECT q.player_id, q.player_name, q.token, q.expires_at
                FROM ltd_manager_bot.qualified_user_info q 
                WHERE q.player_id IN ($placeholders)
                """.trimIndent()

        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    for (i in whiteListIds.indices) {
                        pstmt.setLong(i + 1, whiteListIds[i])
                    }
                    pstmt.executeQuery().use { rs ->
                        val resultMap = mutableMapOf<Long, Triple<String?, String?, Timestamp?>>()
                        while (rs.next()) {
                            val playerId = rs.getLong("player_id")
                            val playerName = rs.getString("player_name")
                            val token = rs.getString("token")
                            val tokenValue = if (rs.wasNull()) null else token
                            val expiresAt = rs.getTimestamp("expires_at")
                            val expiresAtValue = if (rs.wasNull()) null else expiresAt

                            resultMap[playerId] = Triple(playerName, tokenValue, expiresAtValue)
                        }

                        // 直接遍历原始列表，不需要额外的映射
                        for ((playerId, specificMsg) in list) {
                            val mailRequestArgument = resultMap[playerId]

                            if (mailRequestArgument?.second != null && mailRequestArgument.third != null) {
                                mailModule.enqueue(mail {
                                    to += specificMsg.userId.toString() + "@qq.com" // 直接使用 specificMsg
                                    // 根据需要配置邮件内容
                                    subject = "LTD邀请码邮件"
                                    isHtml = true
                                    body = HtmlTemplateUtil.tokenMailHtmlTemplate(
                                        mailRequestArgument.first!!,
                                        mailRequestArgument.second!!,
                                        mailRequestArgument.third!!,
                                        7,2025
                                    )
                                })
                                sendMessage(napCatClient, specificMsg.userId, specificMsg.realId, specificMsg.time,"已发送邮件注意，查收QQ邮箱")
                            } else if (mailRequestArgument?.second != null) {
                                mailModule.enqueue(mail {
                                    to += specificMsg.userId.toString() + "@qq.com" // 直接使用 specificMsg
                                    // 根据需要配置邮件内容
                                    subject = "LTD邀请码邮件"
                                    isHtml = true
                                    body = HtmlTemplateUtil.tokenMailHtmlTemplate(
                                        mailRequestArgument.first!!,
                                        mailRequestArgument.second!!,
                                        timeYear = 2025
                                    )
                                })
                                sendMessage(napCatClient, specificMsg.userId, specificMsg.realId, specificMsg.time,"已发送邮件注意，查收QQ邮箱")
                            } else {
                                LoggerUtil.logger.error("[$name] 异常情况，code为 空值")
                                sendFailedMessage(napCatClient, specificMsg.userId, specificMsg.realId, specificMsg.time, "系统内部异常，请联系管理员")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 查询已获取邀请码但未使用或未过期用户，Code信息失败: ${e.message}", e)
            sendFailedMessage(napCatClient, text = "查询已获取邀请码但未使用或未过期用户，Code信息失败: ${e.message}")
        }
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
        lastTriggerMapState = lastTriggerMapState.updateLastTrigger(qq, realId, time)
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
             lastTriggerMapState = lastTriggerMapState.updateLastTrigger(qq, realId, time)
        } else {
            val request = SendGroupMsgRequest(
                listOf(MessageElement.text(text)),
                ID.long(groupMessagePollingModule.targetGroupId)
            )
            client.sendUnit(request)
            LoggerUtil.logger.info("[$name] 已发送 失败消息[无指定对象]")
        }
    }

    private suspend fun sendCooldownMessage(client: NapCatClient, qq: Long, realId: Long, msg: String) {
        val request = SendGroupMsgRequest(
            MessageElement.reply(ID.long(realId), msg),
            ID.long(groupMessagePollingModule.targetGroupId)
        )
        client.sendUnit(request)
        lastTriggerMapState = lastTriggerMapState.updateLastTrigger(qq, realId, -1)
    }
    private suspend fun createAndSearchInvitationCodeIdsThenUpdateDate(
        needNewTokenIdAndMsgPairs: List<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>,
    ) {
        if (needNewTokenIdAndMsgPairs.isEmpty()) return

        try {
            // 1. 创建邀请码
            val invitationCodes = createInvitationCodes(needNewTokenIdAndMsgPairs.size)

            // 2. 验证数量匹配
            validateCodeCountMatch(invitationCodes, needNewTokenIdAndMsgPairs)

            // 3. 获取邀请码ID
            val codeToIdMap = getInvitationCodeIds(invitationCodes!!.map { it.code })

            // 4. 更新或插入关联关系
            updateInvitationCodeAscription(needNewTokenIdAndMsgPairs.map { it.first }, codeToIdMap.values.toList())

        } catch (e: Exception) {
            handleCreationError(e)
        }
    }

    /**
     * 1. 创建邀请码
     */
    private suspend fun createInvitationCodes(amount: Int): List<InvitationCodeGenerationResponse.InvitationCode>? {
        return try {
            val response = blessingSkinClient.submitRequest(
                GenerateInvitationCodeRequest(amount = amount, token = apiToken)
            )

            when (response) {
                is ResponseResult.Success -> {
                    if (response.response.success) {
                        response.response.data
                    } else {
                        LoggerUtil.logger.warn("[$name] API返回失败: ${response.response.message}")
                        null
                    }
                }
                is ResponseResult.Failure -> {
                    LoggerUtil.logger.warn("[$name] 创建邀请码失败: ${response.failure.failedResult}")
                    null
                }
            }
        } catch (e: Exception) {
            LoggerUtil.logger.error("[$name] 创建邀请码异常", e)
            null
        }
    }

    /**
     * 2. 验证数量匹配
     */
    private fun validateCodeCountMatch(
        invitationCodes: List<InvitationCodeGenerationResponse.InvitationCode>?,
        needNewTokenIdAndMsgPairs: List<Pair<Long, GetFriendMsgHistoryEvent.SpecificMsg>>
    ) {
        if (invitationCodes == null) {
            throw InvitationCodeException.ApiFailureException("获取邀请码请求失败")
        }

        if (invitationCodes.size != needNewTokenIdAndMsgPairs.size) {
            throw InvitationCodeException.QuantityMismatchException(
                expectedCount = needNewTokenIdAndMsgPairs.size,
                actualCount = invitationCodes.size
            )
        }
    }

    /**
     * 3. 获取邀请码ID
     */
    private fun getInvitationCodeIds(invitationCodes: List<String>): Map<String, Long> {
        if (invitationCodes.isEmpty()) return emptyMap()

        val placeholders = invitationCodes.joinToString(",") { "?" }
        val sql = """
        SELECT i.id, i.code
        FROM blessingskin.invitation_codes i 
        WHERE i.code IN ($placeholders)
    """.trimIndent()

        return getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                // 设置参数
                invitationCodes.forEachIndexed { index, code ->
                    pstmt.setString(index + 1, code)
                }

                val resultMap = mutableMapOf<String, Long>()
                pstmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val id = rs.getLong("id")
                        val code = rs.getString("code")
                        resultMap[code] = id
                    }
                }
                resultMap
            }
        }
    }

    /**
     * 4. 更新或插入关联关系
     */
    private fun updateInvitationCodeAscription(playerIds: List<Long>, codeIds: List<Long>) {
        if (playerIds.size != codeIds.size) {
            throw IllegalStateException("playerIds和codeIds数量不匹配: ${playerIds.size} vs ${codeIds.size}")
        }

        if (playerIds.isEmpty()) return

        val placeholders = playerIds.joinToString(",") { "(?, ?)" }
        val sql = """
        INSERT INTO ltd_manager_bot.invitation_code_ascription (id, token_id)
        VALUES $placeholders
        ON DUPLICATE KEY UPDATE token_id = VALUES(token_id)
    """.trimIndent()

        getConnection().use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                var paramIndex = 1
                for (i in playerIds.indices) {
                    pstmt.setLong(paramIndex++, playerIds[i])
                    pstmt.setLong(paramIndex++, codeIds[i])
                }

                val affectedRows = pstmt.executeUpdate()
                LoggerUtil.logger.debug("[$name] 更新了 $affectedRows 条关联记录")
            }
        }
    }

    /**
     * 5. 错误处理
     */
    private suspend fun handleCreationError(e: Exception) {
        when (e) {
            is InvitationCodeException -> {
                LoggerUtil.logger.error("[$name] ${e.message}")
                if (e is InvitationCodeException.QuantityMismatchException) {
                    // 数量不匹配的特殊处理
                    handleQuantityMismatch(e.expectedCount, e.actualCount)
                } else {
                    sendFailedMessage(napCatClient, text = "邀请码创建失败，请联系管理员")
                }
            }
            else -> {
                LoggerUtil.logger.error("[$name] 捕获异常", e)
                sendFailedMessage(napCatClient, text = "系统内部问题，请联系管理员")
            }
        }
    }

    /**
     * 数量不匹配的特殊处理
     */
    private suspend fun handleQuantityMismatch(expectedCount: Int, actualCount: Int) {
        LoggerUtil.logger.error(
            "[$name] 数量不一致BUG，期望: $expectedCount, 实际: $actualCount"
        )
        sendFailedMessage(napCatClient, text = "系统内部BUG，请联系管理员")
        // TODO: 清理已创建的邀请码
        cleanupCreatedInvitationCodes(actualCount)
    }

    /**
     * 清理已创建的邀请码（TODO实现）
     */
    private fun cleanupCreatedInvitationCodes(createdCount: Int) {
        // 实现清理逻辑，删除多余的邀请码
        LoggerUtil.logger.warn("[$name] 需要清理 $createdCount 个邀请码")
    }


    // =========================
    // 状态持久化
    // =========================
    @Serializable
    data class LastTriggerMapState(
        val map: Map<Long, TriggerDetail> = emptyMap()
    ) {
        fun getLastTriggerTime(qq: Long): Long = map[qq]?.time ?: -1
        fun getLastTriggerRealId(qq: Long): Long = map[qq]?.realId ?: -1

        fun updateLastTrigger(qq: Long, realId: Long, time: Long = -1): LastTriggerMapState {
            val old = map[qq]
            val newTime = if (time != -1L) time else old?.time ?: -1
            val newMap = map.toMutableMap().apply {
                put(qq, TriggerDetail(realId, newTime))
            }
            return copy(map = newMap)
        }
        fun updateLastCooldownRealId(qq: Long, realId: Long): LastTriggerMapState {
            val old = map[qq]
            val newMap = map.toMutableMap().apply {
                put(qq, TriggerDetail(
                    realId = old?.realId ?: -1,
                    time = old?.time ?: -1,
                    lastCooldownRealId = realId
                ))
            }
            return copy(map = newMap)
        }
    }

    @Serializable
    data class TriggerDetail(val realId : Long, val time: Long, val lastCooldownRealId: Long = -1L )

    override fun loadState(): LastTriggerMapState {
        return try {
            if (!stateFile.exists()) {
                LoggerUtil.logger.info("[$name] 状态文件不存在，使用默认值")
                return LastTriggerMapState()
            }
            val content = stateFile.readText()
            val state = Json.decodeFromString<LastTriggerMapState>(content)
            LoggerUtil.logger.info("[$name] 成功加载状态: ${state.map}, 文件路径=${stateFile.absolutePath}")
            state
        } catch (e: Exception) {
            LoggerUtil.logger.warn("[$name] 读取状态失败，尝试从备份恢复", e)
            try {
                if (stateBackupFile.exists()) {
                    val backup = stateBackupFile.readText()
                    val state = Json.decodeFromString<LastTriggerMapState>(backup)
                    LoggerUtil.logger.info("[$name] 成功从备份恢复状态: ${state.map}")
                    state
                } else {
                    LastTriggerMapState()
                }
            } catch (e2: Exception) {
                LoggerUtil.logger.error("[$name] 备份也损坏，使用默认值", e2)
                LastTriggerMapState()
            }
        }
    }

    override fun saveState(state: LastTriggerMapState) {
        fileLock.withLock {
            try {
                val json = Json.encodeToString(state)
                // 先写备份
                if (stateFile.exists()) stateFile.copyTo(stateBackupFile, overwrite = true)
                // 写入新文件
                stateFile.writeText(json)
                LoggerUtil.logger.info("[$name] 已保存状态: ${state.map}, 文件路径=${stateFile.absolutePath}")
            } catch (e: Exception) {
                LoggerUtil.logger.error("[$name] 保存状态失败", e)
            }
        }
    }
    // 在 InvitationCodesModule 类中补全：
    override fun info(): String {
        return """
        模块: $name
        功能: 自动处理群组内“申请邀请码”消息
        描述: 
          1. 监听群消息，过滤关键词和冷却
          2. 根据QQ号查询白名单状态
          3. 自动创建或发送邀请码，并通过邮件发送
          4. 已触发和未触发状态会持久化保存
        关键词: $keywords
        冷却时间: ${cooldownMillis / 1000} 秒
        目标群组: ${groupMessagePollingModule.targetGroupId}
    """.trimIndent()
    }

    override fun help(): String {
        return """
        使用说明:
        1. 在群里发送${keywords}触发本模块
        2. 模块会自动判断你的白名单状态
            - 若已使用过邀请码，会提醒你不要重复申请
            - 若已有邀请码但未使用，会重新发送邮件提醒
            - 若未生成邀请码，会调用API生成并发送邮件
        3. 请求过于频繁时，会有冷却提示
        4. 所有操作都有日志记录，可供管理员审计
        5. 异常情况会发送失败提示消息
    """.trimIndent()
    }

}