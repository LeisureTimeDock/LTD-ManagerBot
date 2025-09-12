package top.r3944realms.ltdmanager.module.common.cooldown

/**
 * 冷却结果
 * @param allowed 是否允许触发
 * @param remaining 剩余秒数（如果未允许触发）
 * @param notify 是否可以发送冷却提示
 */
data class CooldownResult(
    val allowed: Boolean,
    val remaining: Long = 0L,
    val notify: Boolean = true
)