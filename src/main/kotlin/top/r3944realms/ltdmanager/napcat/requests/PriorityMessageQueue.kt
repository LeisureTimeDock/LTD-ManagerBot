package top.r3944realms.ltdmanager.napcat.requests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.util.concurrent.PriorityBlockingQueue

/**
 * 线程安全的优先级消息队列
 */
class PriorityMessageQueue {
    private val queue = PriorityBlockingQueue<PrioritizedRequest>()
    private val pendingSignal = Channel<Unit>(Channel.UNLIMITED)

    suspend fun enqueue(request: PrioritizedRequest) {
        queue.put(request)
        pendingSignal.send(Unit) // 通知有新消息
    }
    suspend fun dequeue(): PrioritizedRequest {
        // 队列为空时挂起等待
        if (queue.isEmpty()) {
            pendingSignal.receive()
        }
        return withContext(Dispatchers.IO) {
            queue.take()
        }
    }
    fun size(): Int = queue.size

    fun close() {
        pendingSignal.close()
    }
}