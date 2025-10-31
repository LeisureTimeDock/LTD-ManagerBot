package top.r394realms.ltdmanagertest.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import kotlin.io.use

suspend fun uploadImageWithKtor(filePath: String, apiKey: String): String {
    val client = HttpClient(CIO) {
        // 添加引擎配置
        engine {
            // 增加超时设置
            requestTimeout = 60000
        }
        // 添加日志拦截器来调试
        expectSuccess = false // 不自动抛出异常，让我们自己处理
    }

    return client.use { httpClient ->
        try {
            val file = File(filePath)

            // 检查文件是否存在
            if (!file.exists()) {
                throw Exception("文件不存在: $filePath")
            }

            LoggerUtil.logger.info("开始上传文件: ${file.name}, 大小: ${file.length()} bytes")

            val response = httpClient.post("https://pic.xiaobuawa.top/api/1/upload") {
                // 设置头信息
                headers {
                    append("X-API-Key", apiKey.trim()) // 去除前后空格
                    append("User-Agent", "Mozilla/5.0 (compatible; MyApp/1.0)")
                }

                // 使用正确的 multipart 格式
                setBody(MultiPartFormDataContent(
                    formData {
                        // 使用 appendInput 而不是 append，更接近 curl 的行为
                        appendInput(
                            "source",
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                            }
                        ) {
                            buildPacket {
                                writeFully(file.readBytes())
                            }
                        }
                        append("format", "json")
                    }
                ))
            }

            val statusCode = response.status.value
            val responseText = response.bodyAsText()

            LoggerUtil.logger.info("响应状态码: $statusCode")
            LoggerUtil.logger.info("响应内容: $responseText")

            if (statusCode != 200) {
                throw Exception("上传失败，状态码: $statusCode, 响应: $responseText")
            }

            return@use responseText
        } catch (e: Exception) {
            LoggerUtil.logger.error("上传过程中发生错误: ${e.message}", e)
            throw e
        }
    }
}

// 或者使用另一种更简单的方法
suspend fun uploadImageWithKtorSimple(filePath: String, apiKey: String): String {
    val client = HttpClient(CIO)

    return client.use { httpClient ->
        val file = File(filePath)

        val response = httpClient.submitFormWithBinaryData(
            url = "https://pic.xiaobuawa.top/api/1/upload",
            formData = formData {
                append("source", file.readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                })
                append("format", "json")
            }
        ) {
            header("X-API-Key", apiKey.trim())
        }

        val responseText = response.bodyAsText()
        LoggerUtil.logger.info("简单方法响应: $responseText")
        responseText
    }
}

fun main() = GlobalManager.runBlockingMain {
    // 注意：API Key 前面不要有空格！
    val apiKey = "XXXX"
    val filePath = "./data/temp/icons8-postgresql-96.png"

    try {
        // 先尝试简单方法
        val result = uploadImageWithKtorSimple(filePath, apiKey)
        println("上传成功: $result")
    } catch (e: Exception) {
        println("简单方法失败，尝试详细方法: ${e.message}")
        try {
            val result = uploadImageWithKtor(filePath, apiKey)
            println("详细方法上传成功: $result")
        } catch (e2: Exception) {
            println("所有方法都失败: ${e2.message}")
        }
    }
}