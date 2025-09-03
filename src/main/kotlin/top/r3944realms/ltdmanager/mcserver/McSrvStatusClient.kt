package top.r3944realms.ltdmanager.mcserver

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Minecraft 服务器状态 API 封装类
 */
class McSrvStatusClient : AutoCloseable {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        // API 要求 User-Agent，必须设置
        defaultRequest {
            headers.append("User-Agent", "MyMinecraftStatusClient/1.0")
        }
    }

    private val baseUrl = "https://api.mcsrvstat.us/3"

    /**
     * 获取服务器状态
     * @param address 域名/IP，例如 "play.hypixel.net"
     */
    suspend fun getServerStatus(address: String): McServerStatus {
        return client.get("$baseUrl/$address").body()
    }

    override fun close() {
        client.close()
    }
    companion object {
        fun create(): McSrvStatusClient = McSrvStatusClient()
    }
}