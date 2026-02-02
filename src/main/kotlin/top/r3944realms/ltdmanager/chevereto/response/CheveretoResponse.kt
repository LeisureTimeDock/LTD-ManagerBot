package top.r3944realms.ltdmanager.chevereto.response

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import top.r3944realms.ltdmanager.chevereto.response.v1.CheveretoUploadResponse
import top.r3944realms.ltdmanager.core.client.response.IResponse

@Serializable
abstract class CheveretoResponse (
    @Transient
    override val httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
    @Transient
    override val createTime: Long = System.currentTimeMillis()
) : IResponse {
    companion object {
        // 通用的反序列化方法
        inline fun <reified T : CheveretoResponse> decode(jsonString: String): T {
            return json.decodeFromString(jsonString)
        }
        val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(CheveretoResponse::class) {
                        subclass(FailedCheveretoResponse.Default::class, FailedCheveretoResponse.Default.serializer())
                        subclass(CheveretoUploadResponse::class, CheveretoUploadResponse.serializer())
                    }
                }
            }
        }
    }

}