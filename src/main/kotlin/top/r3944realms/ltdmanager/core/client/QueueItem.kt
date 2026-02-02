package top.r3944realms.ltdmanager.core.client

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.core.client.request.IRequest
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse
import top.r3944realms.ltdmanager.core.client.response.IResponse

open class QueueItem<R: IRequest<T, F>, T:IResponse, F:IFailedResponse>(
    val request: R,
    val deferred: CompletableDeferred<*>,
    val retries: Int,
    val priority: Int,
    val expectsResponse: Boolean

) : Comparable<QueueItem<R, T, F>> {
//    fun getRequest(): R = request
//    fun getDeferred(): CompletableDeferred<*> = deferred
//    fun getRetries(): Int = retries
//    fun getPriority(): Int = priority

    /**
     * @return true 表示返回 BlessingSkinResponse, false 表示 Unit
     */
    fun expectsResponse(): Boolean = expectsResponse
    override fun compareTo(other: QueueItem<R, @UnsafeVariance T, @UnsafeVariance F>): Int = priority.compareTo(other.priority)
}