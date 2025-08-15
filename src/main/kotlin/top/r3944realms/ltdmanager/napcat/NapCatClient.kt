package top.r3944realms.ltdmanager.napcat

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent
import top.r3944realms.ltdmanager.napcat.requests.NapCatRequest
import top.r3944realms.ltdmanager.napcat.requests.PrioritizedRequest
import top.r3944realms.ltdmanager.napcat.requests.PriorityMessageQueue
import kotlin.coroutines.coroutineContext

class NapCatClient(private val wsUrl: String, private val token: String) {
    private val client = HttpClient(CIO) { install(WebSockets) }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger = LoggerFactory.getLogger(NapCatClient::class.java)

    // 请求-响应匹配队列（FIFO）
    private val pendingResponses = Channel<CompletableDeferred<NapCatEvent>>(capacity = Channel.UNLIMITED)
    private val mutex = Mutex()

    // 事件通道（用于非请求响应的消息）
    // 优先级队列（按优先级发送请求）
    private val priorityQueue = PriorityMessageQueue()
    private val eventChannel = Channel<NapCatEvent>(capacity = Channel.UNLIMITED)
    private val _connectionState = MutableStateFlow(false)
    val connectionState = _connectionState.asStateFlow()

    // 子协程引用
    private var receiverJob: Job? = null
    private var senderJob: Job? = null

    suspend fun start() {
        receiverJob = scope.launch { launchReceiver() }
        senderJob = scope.launch { launchSender() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun launchReceiver() {
        try {
            client.wss(
                host = wsUrl.removePrefix("ws://").substringBefore(':'),
                port = wsUrl.substringAfterLast(':').toInt(),
                path = "/"
            ) {
                send(Frame.Text("""{"token":"$token"}"""))
                _connectionState.value = true

                while (true) {
                        when (val frame = incoming.receive()) {
                        is Frame.Text -> {
                            val event = Json.decodeFromString<NapCatEvent>(frame.readText())
                            // 尝试匹配最近的请求
                            if (!pendingResponses.isEmpty) {
                                pendingResponses.tryReceive().getOrNull()?.complete(event)
                            } else {
                                eventChannel.send(event) // 非请求响应的消息
                            }
                        }
                        is Frame.Close -> break
                        else -> {}
                    }
                }
            }
        } finally {
            _connectionState.value = false
            pendingResponses.close()
            eventChannel.close()
            priorityQueue.close()
        }
    }
    private suspend fun launchSender() {
        while (coroutineContext.isActive) {
            try {
                // 从优先级队列取出请求（自动按优先级排序）
                val prioritized = priorityQueue.dequeue()
                val request = prioritized.request

                // 发送前注册响应监听器
                val deferred = CompletableDeferred<NapCatEvent>()
                mutex.withLock {
                    pendingResponses.send(deferred)
                }

                // 发送请求
                client.webSocketSession(wsUrl).send(Frame.Text(Json.encodeToString(request)))

                // 等待响应（超时由外层 sendRequest 控制）
                deferred.await()
            } catch (e: Exception) {
                logger.error("发送请求失败", e)
                delay(1000) // 错误时暂停1秒
            }
        }
    }

    /**
     * 发送带优先级的请求
     * @param priority 优先级（HIGH_PRIORITY/DEFAULT_PRIORITY/LOW_PRIORITY）
     * @param timeout 超时时间（毫秒）
     */
    suspend fun sendRequest(
        request: NapCatRequest,
        priority: Int = PrioritizedRequest.DEFAULT_PRIORITY,
        timeout: Long = 5000
    ): NapCatEvent = withTimeout(timeout) {
        val deferred = CompletableDeferred<NapCatEvent>()
        // 将请求加入优先级队列
        priorityQueue.enqueue(PrioritizedRequest(request, priority))
        deferred.await() // 等待响应（由 launchSender 和 launchReceiver 协作完成）
    }

    val incomingEvents: ReceiveChannel<NapCatEvent> = eventChannel
    private fun cleanup() {
        _connectionState.value = false
        pendingResponses.close()
        eventChannel.close()
        priorityQueue.close()
    }
    fun close() {
        scope.cancel("NapCatClient closed")
        cleanup()
    }
}