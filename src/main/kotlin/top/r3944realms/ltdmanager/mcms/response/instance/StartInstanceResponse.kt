package top.r3944realms.ltdmanager.mcms.response.instance

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.mcms.response.MCSMResponse

@Serializable
data class StartInstanceResponse(
    @Transient
    val status0: Status = Status.Ok,
    val data: StartInstanceData,
    @Transient
    val time0: Long = -1
) : MCSMResponse(status0, time0){
    @Serializable
    data class StartInstanceData(
        val instanceUuid: String
    )
}


