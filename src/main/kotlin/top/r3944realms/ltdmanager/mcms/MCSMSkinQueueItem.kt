package top.r3944realms.ltdmanager.mcms

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.mcms.request.MCSMRequest
import top.r3944realms.ltdmanager.mcms.response.FailedMCSMResponse
import top.r3944realms.ltdmanager.mcms.response.MCSMResponse

data class MCSMSkinQueueItem<out T:MCSMResponse,out F:FailedMCSMResponse>(
    val request: MCSMRequest<T,F>,
    val deferred: CompletableDeferred<*>,
    var retries: Int,
    val priority: Int,
    val expectsResponse: Boolean // true 表示返回 BlessingSkinResponse, false 表示 Unit
) : Comparable<MCSMSkinQueueItem<@UnsafeVariance T, @UnsafeVariance F>> {
    override fun compareTo(other: MCSMSkinQueueItem<@UnsafeVariance T, @UnsafeVariance F>): Int = priority.compareTo(other.priority)
}
