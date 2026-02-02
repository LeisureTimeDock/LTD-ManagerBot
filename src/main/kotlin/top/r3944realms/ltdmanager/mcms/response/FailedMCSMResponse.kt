package top.r3944realms.ltdmanager.mcms.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

@Serializable
open class FailedMCSMResponse(
    @Transient
    val status0: Status = Status.Ok,
    val data: JsonObject? = null,
    @Transient
    val time0: Long = -1,
): MCSMResponse(
   status0, time0
)  {
    @Serializable
    data class ExceptionFailedMCSMResponse(
        @Transient
        val status1: Status = Status.Ok,
        val data0: String? = null,
        @Transient
        val time1: Long = -1,
        @Transient
        val result: String? = null,
    ): FailedMCSMResponse(
        status1, null, time1
    )
}