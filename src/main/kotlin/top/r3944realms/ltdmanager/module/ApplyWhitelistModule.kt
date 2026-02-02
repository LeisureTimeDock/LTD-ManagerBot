package top.r3944realms.ltdmanager.module

class ApplyWhitelistModule(
    moduleName: String,
    private val groupMessagePollingModule: GroupMessagePollingModule,
    private val cooldownMillis: Long = 120_000,
    private val keywords: Set<String> = setOf("申请白名单")
):
    BaseModule(Modules.APPLY_WHITELIST,moduleName) {
    override fun onLoad() {
        TODO("Not yet implemented")
    }

    override suspend fun onUnload() {
        TODO("Not yet implemented")
    }
}