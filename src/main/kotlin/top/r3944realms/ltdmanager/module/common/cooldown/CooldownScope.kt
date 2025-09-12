package top.r3944realms.ltdmanager.module.common

sealed class CooldownScope {
    object Global : CooldownScope()
    object PerUser : CooldownScope()
}