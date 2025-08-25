package top.r3944realms.ltdmanager.napcat

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

data class QueueItem(
    val request: NapCatRequest,
    val deferred: CompletableDeferred<*>,
    var retries: Int,
    val priority: Int,
    val expectsEvent: Boolean // true 表示返回 NapCatEvent, false 表示 Unit
) : Comparable<QueueItem> {
    override fun compareTo(other: QueueItem): Int = priority.compareTo(other.priority)
}
