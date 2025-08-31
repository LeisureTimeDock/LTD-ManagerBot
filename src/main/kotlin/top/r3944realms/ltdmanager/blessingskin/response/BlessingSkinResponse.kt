package top.r3944realms.ltdmanager.blessingskin.response

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import top.r3944realms.ltdmanager.blessingskin.response.invitecode.InvitationCodeGenerationResponse

@Serializable
abstract class BlessingSkinResponse (
    @Transient
    open val httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
    @Transient
    open val createTime: Long = System.currentTimeMillis()
) {
    companion object {
        // 通用的反序列化方法
        inline fun <reified T : BlessingSkinResponse> decode(jsonString: String): T {
            return json.decodeFromString(jsonString)
        }
        val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(BlessingSkinResponse::class) {
                        subclass(FailedBlessingSkinResponse.Default::class, FailedBlessingSkinResponse.Default.serializer())
                        subclass(InvitationCodeGenerationResponse::class, InvitationCodeGenerationResponse.serializer())
                    }
                }
            }
        }

    }
}