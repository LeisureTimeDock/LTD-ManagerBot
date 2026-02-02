package top.r3944realms.ltdmanager.blessingskin

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import top.r3944realms.ltdmanager.blessingskin.request.BlessingSkinRequest
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse
import top.r3944realms.ltdmanager.core.client.IClient
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse
import top.r3944realms.ltdmanager.core.client.response.ResponseResult
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import java.util.*

class BlessingSkinClient private constructor() : IClient<BlessingSkinRequest, BlessingSkinQueueItem, BlessingSkinResponse, FailedBlessingSkinResponse> {
    private val client = HttpClient(CIO) {
        expectSuccess = false

        // 安装 HttpTimeout 插件
        install(HttpTimeout) {
            // 默认超时配置，会被具体请求的配置覆盖
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 15000
        }

    }

    private val blessingSkinServerConfig = YamlConfigLoader.loadBlessingSkinServerConfig()

    // 限流控制
    private val semaphore = Semaphore(5)
    private val requestMutex = Mutex()
    private val requestQueue = PriorityQueue<BlessingSkinQueueItem>(compareBy { it.priority })
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        init()
    }

    override fun getBaseUrl(): String = blessingSkinServerConfig.url!!

    override fun getType(): String = "BlessingSkinClient"

    override fun getClient(): HttpClient = client

    override fun getSemaphore(): Semaphore = semaphore

    override fun getRequestMutex(): Mutex = requestMutex

    override fun getResponseQueue(): PriorityQueue<BlessingSkinQueueItem> = requestQueue

    override fun getScope(): CoroutineScope = scope

    override fun createFailureResponse(exception: Exception?): IFailedResponse {
       return FailedBlessingSkinResponse.Default(exception?.stackTraceToString()?:"ERROR")
    }

    override fun addToQueue(
        request: BlessingSkinRequest,
        deferredC: CompletableDeferred<ResponseResult<BlessingSkinResponse, FailedBlessingSkinResponse>>,
        priority: Int,
        maxRetries: Int
    ): BlessingSkinQueueItem {
        val element = BlessingSkinQueueItem(request, deferredC, priority, maxRetries, false)
        requestQueue.add(element)
        return element
    }

    companion object {
        fun create(): BlessingSkinClient = BlessingSkinClient()
    }
}
