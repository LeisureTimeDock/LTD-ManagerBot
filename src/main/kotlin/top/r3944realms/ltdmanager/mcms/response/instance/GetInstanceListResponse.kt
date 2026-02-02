package top.r3944realms.ltdmanager.mcms.response.instance

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import top.r3944realms.ltdmanager.mcms.response.MCSMResponse

@Serializable
data class GetInstanceListResponse(
    @Transient
    val status0: Status = Status.Ok,
    val data: InstanceListData? = null,
    @Transient
    val time0: Long = -1,
) : MCSMResponse(status0, time0) {
    @Serializable
    data class InstanceListData(
        val maxPage: Int,
        val pageSize: Int,
        val data: List<InstanceDetail>
    )

    @Serializable
    data class InstanceDetail(
        val config: JsonObject? = null, //TODO: 不清楚是干什么的,需验证
        val info: InstanceInfo,
        val instanceUuid: String,
        val processInfo: ProcessInfo,
        val space: Long,
        val started: Int,
        val status: Int
    )

    @Serializable
    data class InstanceInfo(
        val currentPlayers: Int,
        val fileLock: Int,
        val maxPlayers: Int,
        val openFrpStatus: Boolean,
        val playersChart: List<PlayerChartItem>,
        val version: String
    )

    @Serializable
    data class PlayerChartItem(
        val time: Long? = null,
        val players: Int? = null
    )

    @Serializable
    data class ProcessInfo(
        val cpu: Double,
        val memory: Long,
        val ppid: Long,
        val pid: Long,
        val ctime: Long,
        val elapsed: Long,
        val timestamp: Long
    )
}