package top.r3944realms.ltdmanager.chevereto

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.chevereto.request.CheveretoRequest
import top.r3944realms.ltdmanager.chevereto.response.CheveretoResponse
import top.r3944realms.ltdmanager.chevereto.response.FailedCheveretoResponse
import top.r3944realms.ltdmanager.core.client.QueueItem

data class CheveretoQueueItem(
    val request0: CheveretoRequest,
    val deferred0: CompletableDeferred<*>,
    val priority0: Int,
    var retries0: Int,
    val expectsResponse0: Boolean
) : QueueItem<CheveretoRequest, CheveretoResponse, FailedCheveretoResponse>(
    request0, deferred0, retries0, priority0, expectsResponse0
)