package top.r3944realms.ltdmanager.mcms.response.instance

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.mcms.response.MCSMResponse

@Serializable
data class InstanceListResponse(
    val status: Int,
    val data: InstanceListData?,
    val time: Long
) : MCSMResponse