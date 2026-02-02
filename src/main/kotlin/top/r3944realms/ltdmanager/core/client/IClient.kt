package top.r3944realms.ltdmanager.core.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import top.r3944realms.ltdmanager.core.client.request.IRequest
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse
import top.r3944realms.ltdmanager.core.client.response.IResponse
import top.r3944realms.ltdmanager.core.client.response.ResponseResult
import top.r3944realms.ltdmanager.utils.Environment
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.net.URLEncoder
import java.util.*

interface IClient<R: IRequest<T, F>, Q: QueueItem<R, T, F>, T: IResponse, F: IFailedResponse> : AutoCloseable {
    fun getType(): String
    fun getClient(): HttpClient
    fun getSemaphore(): Semaphore
    fun getRequestMutex(): Mutex
    fun getResponseQueue(): PriorityQueue<Q>
    fun getScope(): CoroutineScope
    fun getBaseUrl(): String = "http://localhost:1234"
    fun createFailureResponse(exception: Exception? ): IFailedResponse
    fun init() {
        startQueueProcessor()
    }
    fun startQueueProcessor() {
        getScope().launch {
            while (isActive) {
                val item = getRequestMutex().withLock {
                    getResponseQueue().poll()
                }
                if (item == null) {
                    delay(50)
                    continue
                }
                processQueueItem(item)
            }
        }
    }
    fun addToQueue(request: R,
                    deferredC: CompletableDeferred<ResponseResult<T, F>>,
                    priority: Int = 5,
                    maxRetries: Int = 3): Q
    /**
     * 提交请求
     */
    suspend fun submitRequest(
        request: R,
        priority: Int = 5,
        maxRetries: Int = 3
    ): ResponseResult<T, F> {
        val deferred = CompletableDeferred<ResponseResult<T, F>>()
        getRequestMutex().withLock {
            addToQueue(request, deferred, priority, maxRetries)
        }
        return deferred.await()
    }

    suspend fun processQueueItem(item: Q) {
        getSemaphore().withPermit {
            val request = item.request
            val deferred = item.deferred
            val maxRetries = item.retries
            var attempt = 0
            var lastError: Exception?

            while (attempt < maxRetries) {
                try {
                    val fullUrl = buildFullUrlWithQueryParams(request)
                    if (!Environment.isProduction()) {
                        LoggerUtil.logger.debug("发送请求到: $fullUrl")
                        LoggerUtil.logger.debug("请求方法: {}", request.method())
                    }
                    val response = getClient().request(fullUrl) {
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
                    (deferred as CompletableDeferred<ResponseResult<IResponse, IFailedResponse>>).complete(result)

                    return
                } catch (e: Exception) {
                    lastError = e
                    attempt++

                    if (!request.shouldRetryOnFailure() || attempt >= maxRetries) {
                        break
                    }

                    LoggerUtil.logger.warn("${getType()} 请求失败 (尝试 $attempt/$maxRetries): ${e.message}")
                    delay((attempt * 1000L)) // 指数退避
                }
                // 所有重试都失败或不应重试
                val errorResponse = createFailureResponse(lastError)
                @Suppress("UNCHECKED_CAST")
                (deferred as CompletableDeferred<ResponseResult<IResponse, IFailedResponse>>).complete(
                    ResponseResult.Failure(errorResponse)
                )
            }
        }
    }
    /**
     * 构建完整的URL，包含查询参数
     */
    fun buildFullUrlWithQueryParams(request: R): String {
        val baseUrl = getBaseUrl().removeSuffix("/")
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
    fun isHtmlResponse(text: String): Boolean {
        return text.contains("<!DOCTYPE html>", ignoreCase = true) ||
                text.contains("<html>", ignoreCase = true) ||
                text.contains("Redirecting", ignoreCase = true)
    }
    override fun close() {
        getScope().cancel()
        runBlocking {
            getClient().close()
        }
    }
}