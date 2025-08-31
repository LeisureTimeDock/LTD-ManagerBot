package top.r3944realms.ltdmanager.napcat

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent
import top.r3944realms.ltdmanager.napcat.request.NapCatRequest
import top.r3944realms.ltdmanager.utils.Environment
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.time.Duration.Companion.seconds

class NapCatClient private constructor() : Closeable {
    private val client = HttpClient(CIO)
    private val httpConfig = YamlConfigLoader.loadHttpConfig()
    private val token = httpConfig.decryptedToken

    // 限流 (同时最多 3 个请求)
    private val semaphore = Semaphore(3)

    // 普通优先级队列
    private val requestQueue = PriorityQueue<NapCatQueueItem>(compareBy { it.priority })
    private val queueMutex = Mutex()

    // 紧急队列 (先进先出，最多 10 个)
    private val urgentQueue = ArrayDeque<NapCatQueueItem>(10)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            while (isActive) {
                val item = queueMutex.withLock {
                    when {
                        urgentQueue.isNotEmpty() -> urgentQueue.removeFirst()
                        requestQueue.isNotEmpty() -> requestQueue.poll()
                        else -> null
                    }
                }

                if (item == null) {
                    // 队列空 -> 挂起一小段时间等待新任务
                    delay(20)
                    continue
                }

                processRequest(item)
            }
        }
    }
    /**
     * 普通发送 (带优先级) 无返回事件版本
     * 适用于只需要发送请求，不关心返回结果的情况，例如 SetGroupAddRequestRequest
     */
    suspend fun sendUnit(
        request: NapCatRequest,
        retries: Int = 3,
        priority: Int = 5
    ) {
        checkRequest(request)
        val deferred = CompletableDeferred<Unit>()
        queueMutex.withLock {
            requestQueue.add(NapCatQueueItem(request, deferred, retries, priority, expectsEvent = false))
        }
        deferred.await()
    }

    /**
     * 紧急发送 (先进先出, 最多 10 个) 无返回事件版本
     */
    @Throws(IllegalStateException::class)
    suspend fun sendUrgentUnit(
        request: NapCatRequest,
        retries: Int = 3
    ) {
        checkRequest(request)
        val deferred = CompletableDeferred<Unit>()
        queueMutex.withLock {
            if (urgentQueue.size >= 10) {
                throw IllegalStateException("紧急任务队列已满 (最多 10 个)")
            }
            urgentQueue.addLast(NapCatQueueItem(request, deferred, retries, priority = Int.MIN_VALUE, expectsEvent = false))
        }
        deferred.await()
    }

    /**
     * 普通发送 (带优先级)
     */
    suspend fun <T : NapCatEvent> send(
        request: NapCatRequest,
        retries: Int = 3,
        priority: Int = 5
    ): T {
        checkRequest(request)
        val deferred = CompletableDeferred<T>()
        queueMutex.withLock {
            requestQueue.add(NapCatQueueItem(request, deferred, retries, priority, expectsEvent = true))
        }
        return deferred.await()
    }

    /**
     * 紧急发送 (先进先出, 最多 10 个)
     */
    @Throws(IllegalStateException::class)
    suspend fun <T : NapCatEvent> sendUrgent(
        request: NapCatRequest,
        retries: Int = 3
    ): T {
        checkRequest(request)
        val deferred = CompletableDeferred<T>()
        queueMutex.withLock {
            if (urgentQueue.size >= 10) {
                throw IllegalStateException("紧急任务队列已满 (最多 10 个)")
            }
            urgentQueue.addLast(NapCatQueueItem(request, deferred, retries, priority = Int.MIN_VALUE, expectsEvent = true))
        }
        return deferred.await()
    }
    private fun checkRequest(request: NapCatRequest) {
        // 如果请求类标记为 @Developing，则抛出异常
        if (request::class.annotations.any { it.annotationClass == Developing::class }) {
            throw UnsupportedOperationException(
                "请求类 ${request::class.simpleName} 标记为 @Developing，不支持发送"
            )
        }

    }

    private suspend fun processRequest(item: NapCatQueueItem) {
        semaphore.withPermit {
            val (request, deferred, retries, _, expectsEvent) = item
            var attempt = 0
            var lastError: Throwable? = null

            while (attempt < retries) {
                try {
                    val apiUrl = URLBuilder(httpConfig.url.toString()).apply {
                        encodedPath += request.path()
                    }.build()

                    if(!Environment.isProduction()) LoggerUtil.logger.debug("发送请求: ${request.toJSON()}")

                    val response = client.post(apiUrl) {
                        contentType(ContentType.Application.Json)
                        header("Authorization", "Bearer $token")
                        setBody(request.toJSON())
                    }

                    if (!response.status.isSuccess()) {
                        throw IllegalStateException("请求失败: HTTP ${response.status}")
                    }
                    if (response.contentType()?.match(ContentType.Application.Json) != true && expectsEvent) {
                        throw IllegalStateException("请求失败: 响应类型不是 JSON (${response.contentType()})")
                    }

                    val jsonText: String = response.body()
                    if(!Environment.isProduction()) LoggerUtil.logger.debug("响应: $jsonText")
                    if (expectsEvent) {
                        val event = NapCatEvent.decodeEvent(jsonText, request.type())
                        @Suppress("UNCHECKED_CAST")
                        (deferred as CompletableDeferred<NapCatEvent>).complete(event)
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        (deferred as CompletableDeferred<Unit>).complete(Unit)
                    }
                    return
                } catch (e: Exception) {
                    lastError = e
                    LoggerUtil.logger.warn("请求失败, 第 ${attempt + 1} 次: ${e.message}")
                    delay(((attempt + 1) * 2L).seconds) // 指数退避
                }
                attempt++
            }

            deferred.completeExceptionally(lastError ?: RuntimeException("未知错误"))
        }
    }

    override fun close() {
        scope.cancel()
        runBlocking { client.close() }
    }

    companion object {
        fun create(): NapCatClient = NapCatClient()
    }
}
