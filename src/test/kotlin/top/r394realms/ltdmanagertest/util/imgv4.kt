package top.r394realms.ltdmanagertest.util

import top.r3944realms.ltdmanager.GlobalManager

fun main() = GlobalManager.runBlockingMain {
    // 测试配置
    val apiKey = "XXX"
    val filePath = "./data/temp/icons8-postgresql-96.png"

    println("=== 开始测试图片上传 ===")
    println("API Key: ${apiKey.take(10)}...")
    println("文件路径: $filePath")

    // 测试1: 同步上传
    println("\n--- 测试同步上传 ---")
    try {
        val result = ImageUploader.uploadImage(filePath, apiKey)
        println("✅ 同步上传成功!")
        println("响应结果: ${result.take(200)}...") // 只显示前200个字符
    } catch (e: Exception) {
        println("❌ 同步上传失败: ${e.message}")
        e.printStackTrace()
    }

    // 测试2: 异步上传
    println("\n--- 测试异步上传 ---")
    ImageUploader.uploadImageAsync(filePath, apiKey) { result ->
        result.onSuccess { response ->
            println("✅ 异步上传成功!")
            println("响应结果: ${response.take(200)}...")
        }.onFailure { error ->
            println("❌ 异步上传失败: ${error.message}")
            error.printStackTrace()
        }
    }

    // 等待异步操作完成
    println("等待异步操作完成...")
    Thread.sleep(10000)
    println("=== 测试结束 ===")
}

// 使用 GlobalManager 的测试版本（如果需要）
fun mainWithGlobalManager() = GlobalManager.runBlockingMain {
    val apiKey = "XXXX"
    val filePath = "./data/temp/icons8-postgresql-96.png"

    println("=== 使用 GlobalManager 测试图片上传 ===")

    // 测试同步上传
    try {
        val result = ImageUploader.uploadImage(filePath, apiKey)
        println("✅ 上传成功!")
        println("响应: $result")
    } catch (e: Exception) {
        println("❌ 上传失败: ${e.message}")
        e.printStackTrace()
    }
}

// 简单的单元测试函数
fun testImageUpload() {
    val testCases = listOf(
        // (文件路径, API Key, 期望结果)
        "./data/temp/icons8-postgresql-96.png" to "chv_YmZ_12a0828fd88823ad4ef16a0c551b4a10ae5ce1b3e3eb65b07d87eb30162cbc91ed520334018fce2d6ba06f9d58724cef66d30ab7f6292bd4e33ad5e0d96c6499",
        "./data/temp/nonexistent.png" to "chv_YmZ_12a0828fd88823ad4ef16a0c551b4a10ae5ce1b3e3eb65b07d87eb30162cbc91ed520334018fce2d6ba06f9d58724cef66d30ab7f6292bd4e33ad5e0d96c6499", // 不存在的文件
        "./data/temp/icons8-postgresql-96.png" to "invalid_key" // 无效的 API Key
    )

    testCases.forEachIndexed { index, (filePath, apiKey) ->
        println("\n测试用例 ${index + 1}:")
        println("文件: $filePath")
        println("API Key: ${apiKey.take(10)}...")

        try {
            val result = ImageUploader.uploadImage(filePath, apiKey)
            println("✅ 成功: ${result.take(100)}...")
        } catch (e: Exception) {
            println("❌ 失败: ${e.message}")
        }
    }
}