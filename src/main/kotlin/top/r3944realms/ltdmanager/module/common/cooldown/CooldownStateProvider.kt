package top.r3944realms.ltdmanager.module.common.cooldown

interface CooldownStateProvider<S> {
    fun load(): S
    fun save(state: S)
}