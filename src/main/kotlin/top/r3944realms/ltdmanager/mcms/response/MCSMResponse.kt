package top.r3944realms.ltdmanager.mcms.response

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import top.r3944realms.ltdmanager.mcms.response.instance.GetInstanceListResponse
import top.r3944realms.ltdmanager.mcms.response.instance.StartInstanceResponse


@Serializable
abstract class MCSMResponse (
    open val status: Status,
    open val time: Long,
    @Transient
    open val httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
    @Transient
    open val createTime: Long = System.currentTimeMillis()
) {
    companion object {
        // 通用的反序列化方法
        inline fun <reified T : MCSMResponse> decode(jsonString: String): T {
            return json.decodeFromString(jsonString)
        }
        val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(MCSMResponse::class) {
                        subclass(GetInstanceListResponse::class, GetInstanceListResponse.serializer())
                        subclass(StartInstanceResponse::class, StartInstanceResponse.serializer())
                    }
                }
            }
        }
    }
    @Serializable
    enum class Status(val value: String) {
        @SerialName("200") Ok("200"),
        @SerialName("400") ParamsNotRight("400"),
        @SerialName("403") PermissionDenied("403"),
        @SerialName("500") InternalServerError("500");
        companion object {
            fun isOk(value: Status): Boolean = value == Ok
        }
    }
}