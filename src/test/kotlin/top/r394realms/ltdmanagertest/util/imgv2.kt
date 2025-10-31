package top.r394realms.ltdmanagertest.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.chevereto.data.CheveretoResponse
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun main() = GlobalManager.runBlockingMain {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val filePath = "./data/temp/icons8-postgresql-96.png"
    val file = File(filePath)
    if (!file.exists()) {
        println("文件不存在: ${file.absolutePath}")
        return@runBlockingMain
    }

    val apiKey = "XXXX"

    try {
        // 构建 multipart/form-data
        val formDataContent = formData {
            append("source", file.readBytes(), Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
            })
            append("format", "json")
        }

        // 调试输出每个 part
        formDataContent.forEach { part ->
            println("Part Headers: ${part.headers}")
            when (part) {
                is PartData.FileItem -> println("Part File: ${part.originalFileName}, size=${part.provider()} bytes")
                is PartData.FormItem -> println("Part Form: ${part.value}")
                else -> println("Part Other: $part")
            }
            part.dispose()
        }

        // 发送 POST 请求
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "https://pic.xiaobuawa.top/api/1/upload",
            formData = formDataContent
        ) {
            header ("X-API-Key", apiKey.trim())
        }

        val responseText = response.bodyAsText()
        println("服务器返回原始内容:\n$responseText")

        if (response.status.isSuccess()) {
            val parsed = Json { ignoreUnknownKeys = true }
                .decodeFromString(CheveretoResponse.serializer(), responseText)
            println("上传成功，图片 URL: ${parsed.image?.url}")
        } else {
            println("上传失败，HTTP 状态码: ${response.status}")
        }

    } catch (e: Exception) {
        println("上传过程中出现异常:")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
