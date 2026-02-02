package top.r3944realms.ltdmanager.chevereto

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import top.r3944realms.ltdmanager.chevereto.data.CheveretoSource
import top.r3944realms.ltdmanager.chevereto.request.CheveretoRequest
import top.r3944realms.ltdmanager.chevereto.request.v1.CheveretoUploadRequest
import top.r3944realms.ltdmanager.chevereto.response.CheveretoResponse
import top.r3944realms.ltdmanager.chevereto.response.FailedCheveretoResponse
import top.r3944realms.ltdmanager.chevereto.response.v1.CheveretoUploadResponse
import top.r3944realms.ltdmanager.core.client.IClient
import top.r3944realms.ltdmanager.core.client.response.IFailedResponse
import top.r3944realms.ltdmanager.core.client.response.IResponse
import top.r3944realms.ltdmanager.core.client.response.ResponseResult
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.utils.Environment
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

class CheveretoClient private constructor() :
    IClient<CheveretoRequest, CheveretoQueueItem, CheveretoResponse, FailedCheveretoResponse> {

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

    private val imgTuConfig = YamlConfigLoader.loadTuImgConfig()
    private val baseUrl = imgTuConfig.url!!.removeSuffix("/")
    private val apiKey = imgTuConfig.decryptedPassword!!

    private val semaphore = Semaphore(3)
    private val queue = PriorityQueue<CheveretoQueueItem>()
    private val queueMutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        init()
    }

    override fun getType(): String = "CheveretoClient"

    override fun getClient(): HttpClient = client

    override fun getSemaphore(): Semaphore = semaphore

    override fun getRequestMutex(): Mutex = queueMutex

    override fun getResponseQueue(): PriorityQueue<CheveretoQueueItem> = queue

    override fun getScope(): CoroutineScope = scope

    override fun getBaseUrl(): String = baseUrl

    override fun createFailureResponse(exception: Exception?): FailedCheveretoResponse =
        FailedCheveretoResponse.Default(
            httpStatusCode = HttpStatusCode.InternalServerError,
            failedMessage = exception?.message ?: "Unknown error"
        )

    override fun addToQueue(
        request: CheveretoRequest,
        deferredC: CompletableDeferred<ResponseResult<CheveretoResponse, FailedCheveretoResponse>>,
        priority: Int,
        maxRetries: Int
    ): CheveretoQueueItem {
        val item = CheveretoQueueItem(request, deferredC, maxRetries, priority, true)
        queue.add(item)
        return item
    }

    override suspend fun processQueueItem(item: CheveretoQueueItem) {
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
                            header("X-API-Key", apiKey)
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
     * 上传 File
     */
    suspend fun uploadFile(
        file: File,
        title: String? = null,
        description: String? = null,
        tags: String? = null,
        albumId: String? = null,
        categoryId: String? = null,
        width: Int? = null,
        expiration: String? = null,
        nsfw: Int? = null,
        format: String = "json",
        useFileDate: Int? = null,
        priority: Int = 5,
        maxRetries: Int = 3

    ): CheveretoResponse {
        upload(CheveretoUploadRequest(
            source = CheveretoSource.ByteArraySource(file.readBytes(), file.name),
            format = format,
            title = title,
            description = description,
            tags = tags,
            albumId = albumId,
            categoryId = categoryId,
            width = width,
            expiration = expiration,
            nsfw = nsfw,
            useFileDate = useFileDate
        ), priority, maxRetries).getRetResponse()
        throw Exception("Never Reach")
    }


    /**
     * 上传 ByteArrayInputStream
     */
    suspend fun uploadStream(
        inputStream: ByteArrayInputStream,
        fileName: String,
        title: String? = null,
        description: String? = null,
        tags: String? = null,
        albumId: String? = null,
        categoryId: String? = null,
        width: Int? = null,
        expiration: String? = null,
        nsfw: Int? = null,
        format: String = "json",
        useFileDate: Int? = null,
        priority: Int = 5,
        maxRetries: Int = 3
    ): CheveretoResponse {
        upload(CheveretoUploadRequest(
            source = CheveretoSource.ByteArraySource(inputStream.readBytes(), fileName),
            format = format,
            title = title,
            description = description,
            tags = tags,
            albumId = albumId,
            categoryId = categoryId,
            width = width,
            expiration = expiration,
            nsfw = nsfw,
            useFileDate = useFileDate
        ), priority, maxRetries).getRetResponse()
        throw Exception("Never Reach")
    }

    /**
     * 上传网络图片 URL
     */
    suspend fun uploadUrl(
        url: String,
        title: String? = null,
        description: String? = null,
        tags: String? = null,
        albumId: String? = null,
        categoryId: String? = null,
        width: Int? = null,
        expiration: String? = null,
        nsfw: Int? = null,
        format: String = "json",
        useFileDate: Int? = null,
        priority: Int = 5,
        maxRetries: Int = 3
    ): CheveretoResponse {
        upload(CheveretoUploadRequest(
            source = CheveretoSource.UrlSource(url),
            format = format,
            title = title,
            description = description,
            tags = tags,
            albumId = albumId,
            categoryId = categoryId,
            width = width,
            expiration = expiration,
            nsfw = nsfw,
            useFileDate = useFileDate
        ), priority, maxRetries).getRetResponse()
        throw Exception("Never Reach")
    }

    suspend fun upload(
        request: CheveretoUploadRequest, priority: Int, maxRetries: Int
    ): ResponseResult<CheveretoUploadResponse, FailedCheveretoResponse> {
        return try {
            @Suppress("UNCHECKED_CAST")
            submitRequest(request, priority, maxRetries) as ResponseResult<CheveretoUploadResponse, FailedCheveretoResponse>
        } catch (e: Exception) {
            ResponseResult.Failure(
                FailedCheveretoResponse.Default(
                    httpStatusCode = HttpStatusCode.InternalServerError,
                    failedMessage = "Byte array upload failed: ${e.message}"
                )
            )
        }
    }

    companion object {
        fun create(): CheveretoClient = CheveretoClient()
    }
}