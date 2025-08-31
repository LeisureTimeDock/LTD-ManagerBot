package top.r3944realms.ltdmanager.blessingskin

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import top.r3944realms.ltdmanager.blessingskin.request.BlessingSkinRequest
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.ResponseResult
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.utils.Environment
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.net.URLEncoder
import java.util.*

class BlessingSkinClient private constructor() : AutoCloseable {
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
    private val requestQueue = PriorityQueue<BlessingSkinQueueItem<BlessingSkinResponse, FailedBlessingSkinResponse>>(compareBy { it.priority })
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startQueueProcessor()
    }

    /**
     * 提交请求
     */
    suspend fun <T : BlessingSkinResponse, F : FailedBlessingSkinResponse> submitRequest(
        request: BlessingSkinRequest<T, F>,
        priority: Int = 5,
        maxRetries: Int = 3
    ): ResponseResult<T, F> {
        val deferred = CompletableDeferred<ResponseResult<T, F>>()
        requestMutex.withLock {
            requestQueue.add(BlessingSkinQueueItem(request, deferred, priority, maxRetries, true))
        }
        return deferred.await()
    }

    /**
     * 启动队列处理器
     */
    private fun startQueueProcessor() {
        scope.launch {
            while (isActive) {
                val item = requestMutex.withLock {
                    requestQueue.poll()
                }
                if (item == null) {
                    delay(50)
                    continue
                }
                processQueueItem(item)
            }
        }
    }

    /**
     * 处理队列项
     */
    private suspend fun processQueueItem(item: BlessingSkinQueueItem<BlessingSkinResponse, FailedBlessingSkinResponse>) {
        semaphore.withPermit {
            val (request, deferred, _, maxRetries, expectsResponse) = item
            var attempt = 0
            var lastError: Exception? = null

            while (attempt < maxRetries) {
                try {
                    // 构建完整的URL，包括查询参数
                    val fullUrl = buildFullUrlWithQueryParams(request)

                    if (!Environment.isProduction()) {
                        LoggerUtil.logger.debug("发送请求到: $fullUrl")
                        LoggerUtil.logger.debug("请求方法: {}", request.method())
                    }

                    val response = client.request(fullUrl) {
                        method = request.method()


                        // 设置请求头
                        headers {
                            request.headers().invoke(this)
                        }

                        // 对于非GET请求，设置请求体
                        if (request.method() != HttpMethod.Get) {
                            setBody(request.toJSON())
                        }
                    }

                    val responseText: String = response.body()

                    if (!Environment.isProduction()) {
                        LoggerUtil.logger.debug("响应状态: {}", response.status)
                        LoggerUtil.logger.debug("响应内容: $responseText")
                    }

                    // 检查是否是HTML响应（重定向）
                    if (isHtmlResponse(responseText)) {
                        throw IllegalStateException("接收到HTML重定向响应，请检查API URL配置")
                    }

                    // 解析响应
                    val result = request.getResponse(responseText, response.status)

                    @Suppress("UNCHECKED_CAST")
                    (deferred as CompletableDeferred<ResponseResult<BlessingSkinResponse, FailedBlessingSkinResponse>>).complete(result)

                    return

                } catch (e: Exception) {
                    lastError = e
                    attempt++

                    if (!request.shouldRetryOnFailure() || attempt >= maxRetries) {
                        break
                    }

                    LoggerUtil.logger.warn("BlessingSkin请求失败 (尝试 $attempt/$maxRetries): ${e.message}")
                    delay((attempt * 1000L)) // 指数退避
                }
            }

            // 所有重试都失败或不应重试
            val errorResponse = createFailureResponse(lastError, request)
            @Suppress("UNCHECKED_CAST")
            (deferred as CompletableDeferred<ResponseResult<BlessingSkinResponse, FailedBlessingSkinResponse>>).complete(
                ResponseResult.Failure(errorResponse)
            )
        }
    }

    /**
     * 构建完整的URL，包含查询参数
     */
    private fun buildFullUrlWithQueryParams(request: BlessingSkinRequest<*, *>): String {
        val baseUrl = blessingSkinServerConfig.url?.removeSuffix("/")
        val path = request.path().removePrefix("/")

        // 构建基础URL
        val urlBuilder = StringBuilder("$baseUrl/$path")

        // 添加查询参数
        val queryParams = request.queryParameters().entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }

        if (queryParams.isNotEmpty()) {
            urlBuilder.append("?").append(queryParams)
        }

        return urlBuilder.toString()
    }

    /**
     * 检查是否是HTML响应
     */
    private fun isHtmlResponse(text: String): Boolean {
        return text.contains("<!DOCTYPE html>", ignoreCase = true) ||
                text.contains("<html>", ignoreCase = true) ||
                text.contains("Redirecting", ignoreCase = true)
    }

    /**
     * 创建失败响应
     */
    private fun createFailureResponse(
        exception: Exception?,
        request: BlessingSkinRequest<*, *>
    ): FailedBlessingSkinResponse {
        return FailedBlessingSkinResponse.Default(
            failedResult = exception?.message ?: "未知错误",
        )
    }

    override fun close() {
        scope.cancel()
        runBlocking {
            client.close()
        }
    }

    companion object {
        fun create(): BlessingSkinClient = BlessingSkinClient()
    }
}
