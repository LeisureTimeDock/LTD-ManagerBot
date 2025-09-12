package top.r3944realms.ltdmanager.module.common

data class CooldownResult(
    val canTrigger: Boolean,
    val remainingSeconds: Long = 0
)