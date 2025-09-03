package top.r3944realms.ltdmanager.mcserver

import kotlinx.serialization.Serializable

/**
 * 数据模型：服务器状态响应
 */
@Serializable
data class McServerStatus(
    val online: Boolean,
    val ip: String? = null,
    val port: Int? = null,
    val hostname: String? = null,
    val version: String? = null,
    val software: String? = null,
    val motd: Motd? = null,
    val players: Players? = null,
    val debug: Debug? = null
) {
    @Serializable
    data class Motd(
        val raw: List<String>? = null,
        val clean: List<String>? = null,
        val html: List<String>? = null
    )

    @Serializable
    data class Players(
        val online: Int = 0,
        val max: Int = 0,
        val list: List<Player>? = null
    ) {
        @Serializable
        data class Player(
            val name: String,
            val uuid: String
        )
    }

    @Serializable
    data class Debug(
        val ping: Boolean? = null,
        val query: Boolean? = null,
        val bedrock: Boolean? = null,
        val srv: Boolean? = null,
        val querymismatch: Boolean? = null,
        val ipinsrv: Boolean? = null,
        val cnameinsrv: Boolean? = null,
        val animatedmotd: Boolean? = null,
        val cachehit: Boolean? = null,
        val cachetime: Long? = null,
        val cacheexpire: Long? = null,
        val apiversion: Int? = null,
        val error: Map<String, String?>? = null // 关键：记录 ping/query 等错误信息
    )
}
