package top.r394realms.ltdmanagertest.util


import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.io.File
import java.io.IOException

object ImageUploader {

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 查看完整的请求和响应
        })
        .build()

    fun uploadImage(filePath: String, apiKey: String): String {
        val file = File(filePath)

        // 检查文件是否存在
        if (!file.exists()) {
            throw IllegalArgumentException("文件不存在: $filePath")
        }

        LoggerUtil.logger.info("开始上传文件: ${file.name}, 大小: ${file.length()} bytes")

        // 创建 multipart 请求体
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "source",
                file.name,
                file.asRequestBody("image/png".toMediaType())
            )
            .addFormDataPart("format", "json")
            .build()

        // 创建请求
        val request = Request.Builder()
            .url("https://pic.xiaobuawa.top/api/1/upload")
            .header("X-API-Key", apiKey.trim()) // 重要：去除空格
            .header("User-Agent", "OkHttp/4.12.0") // 添加 User-Agent
            .post(requestBody)
            .build()

        // 执行请求
        val response = client.newCall(request).execute()
        try {
            if (!response.isSuccessful) {
                throw IOException("上传失败，状态码: ${response.code}, 响应: ${response.body?.string()}")
            }

            val responseBody = response.body?.string()
            LoggerUtil.logger.info("上传成功: $responseBody")
            return responseBody ?: throw IOException("响应体为空")
        } finally {
            response.close()
        }
    }

    // 异步版本（推荐用于生产环境）
    fun uploadImageAsync(filePath: String, apiKey: String, callback: (Result<String>) -> Unit) {
        val file = File(filePath)

        if (!file.exists()) {
            callback(Result.failure(IllegalArgumentException("文件不存在: $filePath")))
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "source",
                file.name,
                file.asRequestBody("image/png".toMediaType())
            )
            .addFormDataPart("format", "json")
            .build()

        val request = Request.Builder()
            .url("https://pic.xiaobuawa.top/api/1/upload")
            .header("X-API-Key", apiKey.trim())
            .header("User-Agent", "OkHttp/4.12.0")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful) {
                        callback(Result.failure(IOException("上传失败，状态码: ${response.code}")))
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        callback(Result.success(responseBody))
                    } else {
                        callback(Result.failure(IOException("响应体为空")))
                    }
                } catch (e: Exception) {
                    callback(Result.failure(e))
                }
            }
        })
    }
}