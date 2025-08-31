package top.r3944realms.ltdmanager.napcat

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest

data class NapCatQueueItem(
    val request: NapCatRequest,
    val deferred: CompletableDeferred<*>,
    var retries: Int,
    val priority: Int,
    val expectsEvent: Boolean // true 表示返回 NapCatEvent, false 表示 Unit
) : Comparable<NapCatQueueItem> {
    override fun compareTo(other: NapCatQueueItem): Int = priority.compareTo(other.priority)
}
