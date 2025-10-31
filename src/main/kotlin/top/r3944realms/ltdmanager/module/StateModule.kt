package top.r3944realms.ltdmanager.module

import kotlinx.coroutines.*
import top.r3944realms.ltdmanager.napcat.request.account.SetQQProfileRequest

//TODO: 有问题不要使用 #unload得考虑下怎么写
class StateModule(
    moduleName: String,
    private val onlineName: String,
    private val offlineName: String,
): BaseModule("StateModule", moduleName) {
    private var scope: CoroutineScope? = null
    override fun onLoad() {
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            if (loaded) updateProfile(onlineName)
        }
    }
    private suspend fun updateProfile(name: String) {
        napCatClient.sendUrgentUnit(SetQQProfileRequest(name))
    }

    override suspend fun onUnload() {
        updateProfile(offlineName)
        scope!!.cancel()
    }
}