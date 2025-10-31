package top.r3944realms.ltdmanager.chevereto

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.chevereto.data.CheveretoResponse
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque


class CheveretoClient private constructor() : Closeable {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    private val imgTuConfig = YamlConfigLoader.loadTuImgConfig()
    private val apiUrl = imgTuConfig.url!!
    private val apiKey = imgTuConfig.decryptedPassword!!
    // 限流，同时最多 3 个上传
    private val semaphore = Semaphore(3)

    // 普通队列 (按 priority 排序)
    private val queue = PriorityQueue<CheveretoQueueItem<CheveretoResponse>>(compareBy { it.priority })
    private val queueMutex = Mutex()

    // 紧急队列 (FIFO，最多 10 个)
    private val urgentQueue = ArrayDeque<CheveretoQueueItem<CheveretoResponse>>(10)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            while (isActive) {
                val item = queueMutex.withLock {
                    when {
                        urgentQueue.isNotEmpty() -> urgentQueue.removeFirst()
                        queue.isNotEmpty() -> queue.poll()
                        else -> null
                    }
                }
                if (item != null) processItem(item)
                else delay(20)
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
        priority: Int = 5
    ): CheveretoResponse {
        val deferred = CompletableDeferred<CheveretoResponse>()
        val source = suspend {
            safeUpload {
                submitFormWithBinaryData(
                    url = apiUrl,
                    formData = formData {
                        append("source", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"source\"; filename=\"${file.name}\"")
                        })
                        append("format", format)
                        title?.let { append("title", it) }
                        description?.let { append("description", it) }
                        tags?.let { append("tags", it) }
                        albumId?.let { append("album_id", it) }
                        categoryId?.let { append("category_id", it) }
                        width?.let { append("width", it.toString()) }
                        expiration?.let { append("expiration", it) }
                        nsfw?.let { append("nsfw", it.toString()) }
                        useFileDate?.let { append("use_file_date", it.toString()) }
                    }
                ) {
                    header("X-API-Key", apiKey)
                }
            }
        }
        queueMutex.withLock { queue.add(CheveretoQueueItem(source, deferred, priority)) }
        return deferred.await()
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
        priority: Int = 5
    ): CheveretoResponse {
        val deferred = CompletableDeferred<CheveretoResponse>()
        val source = suspend {
            val bytes = inputStream.readBytes()
            safeUpload {
                submitFormWithBinaryData(
                    url = apiUrl,
                    formData = formData {
                        append("source", bytes, Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"source\"; filename=\"$fileName\"")
                        })
                        append("format", format)
                        title?.let { append("title", it) }
                        description?.let { append("description", it) }
                        tags?.let { append("tags", it) }
                        albumId?.let { append("album_id", it) }
                        categoryId?.let { append("category_id", it) }
                        width?.let { append("width", it.toString()) }
                        expiration?.let { append("expiration", it) }
                        nsfw?.let { append("nsfw", it.toString()) }
                        useFileDate?.let { append("use_file_date", it.toString()) }
                    }
                ) {
                    header("X-API-Key", apiKey)
                }
            }
        }
        queueMutex.withLock { queue.add(CheveretoQueueItem(source, deferred, priority)) }
        return deferred.await()
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
        priority: Int = 5
    ): CheveretoResponse {
        val deferred = CompletableDeferred<CheveretoResponse>()
        val source = suspend {
            safeUpload {
                submitForm(
                    url = apiUrl,
                    formParameters = Parameters.build {
                        append("source", url)
                        append("format", format)
                        title?.let { append("title", it) }
                        description?.let { append("description", it) }
                        tags?.let { append("tags", it) }
                        albumId?.let { append("album_id", it) }
                        categoryId?.let { append("category_id", it) }
                        width?.let { append("width", it.toString()) }
                        expiration?.let { append("expiration", it) }
                        nsfw?.let { append("nsfw", it.toString()) }
                        useFileDate?.let { append("use_file_date", it.toString()) }
                    }
                ) {
                    header("X-API-Key", apiKey)
                }
            }
        }
        queueMutex.withLock { queue.add(CheveretoQueueItem(source, deferred, priority)) }
        return deferred.await()
    }

    private suspend fun processItem(item: CheveretoQueueItem<CheveretoResponse>) {
        semaphore.withPermit {
            try {
                val result = item.source()
                item.deferred.complete(result)
            } catch (e: Exception) {
                item.deferred.completeExceptionally(e)
            }
        }
    }
    /**
     * 包装上传，失败时打印原始响应
     */
    private suspend fun safeUpload(block: suspend HttpClient.() -> HttpResponse): CheveretoResponse {
        val response = client.block()
        return try {
            response.body()
        } catch (e: Exception) {
            val raw = response.bodyAsText()
            throw RuntimeException("Upload failed (status=${response.status}): $raw", e)
        }
    }


    override fun close() {
        scope.cancel()
        runBlocking { client.close() }
    }

    companion object {
        fun create(): CheveretoClient = CheveretoClient()
    }
}