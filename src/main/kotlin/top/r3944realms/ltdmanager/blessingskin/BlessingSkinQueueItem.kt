package top.r3944realms.ltdmanager.blessingskin

import kotlinx.coroutines.CompletableDeferred
import top.r3944realms.ltdmanager.blessingskin.request.BlessingSkinRequest
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse
import top.r3944realms.ltdmanager.core.client.QueueItem

data class BlessingSkinQueueItem (
    val request0: BlessingSkinRequest,
    val deferred0: CompletableDeferred<*>,
    val priority0: Int,
    var retries0: Int,
    val expectsResponse0: Boolean
) : QueueItem<BlessingSkinRequest, BlessingSkinResponse, FailedBlessingSkinResponse> (
    request0, deferred0, retries0, priority0, expectsResponse0
)
