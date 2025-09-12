package top.r3944realms.ltdmanager.module.common.cooldown

import top.r3944realms.ltdmanager.GlobalManager.napCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.request.group.SetGroupBanRequest
import kotlin.math.max


/**
 * 管理冷却
 * @param S 状态类型
 */
class CooldownManager<S>(
    private val cooldownMillis: Long,
    private val scope: CooldownScope,
    private val stateProvider: CooldownStateProvider<S>,
    private val getLastTrigger: (S, Long?) -> Pair<Long, Long>, // (lastTimeSec, lastRealId)
    private val updateTrigger: (S, Long?, Long, Long) -> S, // 更新 lastTimeSec, lastRealId
    private val updateCooldownRealId: (S, Long?, Long) -> S,
    private val groupId: Long, // 所属群组 ID，用于禁言
    private val banSeconds: Int = 60 // 重复发送禁言时间
) {

    private var state: S = stateProvider.load()

    /**
     * 检查冷却
     * @param userId PerUser 模式必须
     * @param realId 消息 realId
     */
    suspend fun checkAndHandle(userId: Long?, realId: Long): CooldownResult {
        require(scope != CooldownScope.PerUser || userId != null) { "userId required for per-user cooldown" }

        val (lastTimeSec, lastCooldownRealId) = getLastTrigger(state, userId)
        val cooldownSec = cooldownMillis / 1000

        val now = System.currentTimeMillis() / 1000
        val elapsed = if (lastTimeSec == -1L) Long.MAX_VALUE else now - lastTimeSec

        return if (elapsed >= cooldownSec) {
            // ✅ 冷却结束，允许处理消息
            state = updateTrigger(state, userId, realId, now)
            stateProvider.save(state)
            CooldownResult(allowed = true, remaining = 0, notify = false)
        } else {
            val remaining = max(0, cooldownSec - elapsed)
            val notify = realId != lastCooldownRealId // 第一次触发冷却提示

            if (notify) {
                // 第一次冷却提示，记录消息 ID
                state = updateCooldownRealId(state, userId, realId)
                stateProvider.save(state)
            } else {
//                // ⚠️ 重复发送冷却消息 -> 禁言
//                if (userId != null) {
//                    banUser(userId, groupId, banSeconds)
//                }
            }

            CooldownResult(allowed = false, remaining = remaining, notify = notify)
        }
    }

    private suspend fun banUser(userId: Long, groupId: Long, seconds: Int) {
        val request = SetGroupBanRequest(
            duration = seconds.toDouble(),
            groupId = ID.long(groupId),
            userId = ID.long(userId)
        )
        napCatClient.sendUnit(request)
    }
}