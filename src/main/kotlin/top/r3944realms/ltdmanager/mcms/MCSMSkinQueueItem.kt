package top.r3944realms.ltdmanager.blessingskin

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.blessingskin.request.BlessingSkinRequest
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse

data class BlessingSkinQueueItem<out T:BlessingSkinResponse,out F:FailedBlessingSkinResponse>(
    val request: BlessingSkinRequest<T,F>,
    val deferred: CompletableDeferred<*>,
    var retries: Int,
    val priority: Int,
    val expectsResponse: Boolean // true 表示返回 BlessingSkinResponse, false 表示 Unit
) : Comparable<BlessingSkinQueueItem<@UnsafeVariance T, @UnsafeVariance F>> {
    override fun compareTo(other: BlessingSkinQueueItem<@UnsafeVariance T, @UnsafeVariance F>): Int = priority.compareTo(other.priority)
}
