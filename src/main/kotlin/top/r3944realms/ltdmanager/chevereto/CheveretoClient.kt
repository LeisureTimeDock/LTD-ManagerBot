package top.r3944realms.ltdmanager.chevereto

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.File
import java.util.*


object CheveretoUploader {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * 上传本地文件
     */
    suspend fun uploadFile(
        apiUrl: String,
        apiKey: String,
        file: File,
        title: String? = null,
        description: String? = null
    ): CheveretoResponse {
        return client.submitFormWithBinaryData(
            url = apiUrl,
            formData = formData {
                append("source", file.readBytes(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"source\"; filename=\"${file.name}\"")
                })
                append("format", "json")
                title?.let { append("title", it) }
                description?.let { append("description", it) }
            }
        ) {
            headers {
                append("X-API-Key", apiKey)
            }
        }.body()
    }

    /**
     * 上传网络图片 URL
     */
    suspend fun uploadFromUrl(
        apiUrl: String,
        apiKey: String,
        imageUrl: String
    ): CheveretoResponse {
        return client.submitForm(
            url = apiUrl,
            formParameters = Parameters.build {
                append("source", imageUrl)
                append("format", "json")
            }
        ) {
            headers {
                append("X-API-Key", apiKey)
            }
        }.body()
    }
    /**
     * 上传 ByteArrayInputStream
     */
    suspend fun uploadFromStream(
        apiUrl: String,
        apiKey: String,
        inputStream: ByteArrayInputStream,
        fileName: String,
        title: String? = null,
        description: String? = null
    ): CheveretoResponse {
        val bytes = inputStream.readBytes()
        return client.submitFormWithBinaryData(
            url = apiUrl,
            formData = formData {
                append("source", bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"source\"; filename=\"$fileName\"")
                })
                append("format", "json")
                title?.let { append("title", it) }
                description?.let { append("description", it) }
            }
        ) {
            headers { append("X-API-Key", apiKey) }
        }.body()
    }
}