package top.r394realms.ltdmanagertest.util

import top.r3944realms.ltdmanager.GlobalManager
import java.io.ByteArrayInputStream
import java.io.File

fun main() = GlobalManager.runBlockingMain {
    val client = GlobalManager.cheveretoClient;
    client.use { cheveretoClient ->
        // 1. 测试 File 上传
        val file = File("data/temp/icons8-postgresql-96.png")
        val resp1 = cheveretoClient.uploadFile(file, title = "PostgreSQL Logo", tags = "db,icon,test")
        println("File 上传结果: ${resp1.statusCode} -> ${resp1.image?.url}")

        // 2. 测试 ByteArrayInputStream 上传
        val bytes = file.readBytes()
        val inputStream = ByteArrayInputStream(bytes)
        val resp2 = cheveretoClient.uploadStream(inputStream, fileName = "test", title = "From Stream", description = "测试 ByteArrayInputStream 上传")
        println("Stream 上传结果: ${resp2.statusCode} -> ${resp2.image?.url}")

        // 3. 测试 URL 上传
        val testUrl = "https://img.icons8.com/color/96/postgresql.png"
        val resp3 = cheveretoClient.uploadUrl(testUrl)
        println("URL 上传结果: ${resp3.statusCode} -> ${resp3.image?.url}")
        if (resp3.statusCode == 400) {
            println(resp3.statusTxt)
        }
    }
}