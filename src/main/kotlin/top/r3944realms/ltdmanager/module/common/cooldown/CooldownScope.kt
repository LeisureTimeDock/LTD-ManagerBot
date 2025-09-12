package top.r3944realms.ltdmanager.module.common.cooldown

sealed class CooldownScope {
    data object Global : CooldownScope()
    data object PerUser : CooldownScope()
}