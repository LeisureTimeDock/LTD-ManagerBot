package top.r3944realms.ltdmanager.core.client

import top.r3944realms.ltdmanager.core.client.request.IRequest
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse
import top.r3944realms.ltdmanager.core.client.response.IResponse
import java.util.concurrent.CompletableFuture

interface IQueueItem<out T:IResponse, out F:IFailedResponse> : Comparable<IQueueItem<@UnsafeVariance T, @UnsafeVariance F>> {
    fun getRequest(): IRequest<T,F>
    fun getDeferred(): CompletableFuture<*>
    fun getRetries(): Int
    fun getPriority(): Int

    /**
     * @return true 表示返回 BlessingSkinResponse, false 表示 Unit
     */
    fun expectsResponse(): Boolean
    override fun compareTo(other: IQueueItem<@UnsafeVariance T, @UnsafeVariance F>): Int = getPriority().compareTo(other.getPriority())
}