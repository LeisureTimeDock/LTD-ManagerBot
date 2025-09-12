package top.r3944realms.ltdmanager.module.common

class CooldownManager<S>(
    private val cooldownMillis: Long,
    private val scope: CooldownScope,
    private val stateProvider: CooldownStateProvider<S>,
    private val getLastTrigger: (S, Long?) -> Pair<Long, Long>, // (time, realId)
    private val updateTrigger: (S, Long?, Long, Long) -> S,     // (qq, realId, time)
    private val updateCooldownRealId: (S, Long?, Long) -> S     // (qq, realId)
) {
    private var state: S = stateProvider.load()

    fun check(userId: Long?, realId: Long, msgTime: Long): CooldownResult {
        val (lastTime, lastCooldownRealId) = getLastTrigger(state, if (scope == CooldownScope.PerUser) userId else null)
        val nowSec = System.currentTimeMillis() / 1000
        val cooldownSec = cooldownMillis / 1000

        return if (lastTime == -1L || nowSec - lastTime >= cooldownSec) {
            state = updateTrigger(state, userId, realId, msgTime)
            stateProvider.save(state)
            CooldownResult(true)
        } else {
            if (realId != lastCooldownRealId) {
                state = updateCooldownRealId(state, userId, realId)
                stateProvider.save(state)
            }
            CooldownResult(false, cooldownSec - (nowSec - lastTime))
        }
    }
}