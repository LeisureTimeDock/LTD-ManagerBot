package top.r3944realms.ltdmanager.chevereto

import kotlinx.coroutines.CompletableDeferred

data class CheveretoQueueItem<T>(
    val source: suspend () -> T,
    val deferred: CompletableDeferred<T>,
    val priority: Int = 5
)