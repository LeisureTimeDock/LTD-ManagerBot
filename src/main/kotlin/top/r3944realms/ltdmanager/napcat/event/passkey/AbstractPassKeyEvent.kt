package top.r3944realms.ltdmanager.napcat.event.passkey

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

/**
 * QQ 密钥相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
*/
@Serializable
abstract class AbstractPassKeyEvent (
    /**
     * 状态字符串
     */
    open val status: Status,
    /**
     * 返回代码
     */
    open val retcode: Double,
    /**
     * 消息
     */
    open val message: String,
    /**
     * 文字描述
     */
    open val wording: String,
    /**
     * 回显字段
     */
    open val echo: String? = null
) : NapCatEvent() {
    override fun type(): String = "passkey/" + subtype()

    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("passkey/get_clientkey", GetClientkeyEvent.serializer())
                put("passkey/get_cookies", GetCookiesEvent.serializer())
                put ("passkey/get_csrf_token", GetCsrfTokenEvent.serializer())
                put("passkey/get_credentials", GetCredentialsEvent.serializer())
                put("passkey/get_rkey", GetRkeyEvent.serializer())
                put("passkey/nc_get_rkey", NcGetRkeyEvent.serializer())
                put("passkey/get_rkey_server", GetRkeyServerEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(GetClientkeyEvent::class)
                        subclass(GetCookiesEvent::class)
                        subclass(GetCsrfTokenEvent::class)
                        subclass(GetCredentialsEvent::class)
                        subclass(GetRkeyEvent::class)
                        subclass(NcGetRkeyEvent::class)
                        subclass(GetRkeyServerEvent::class)
                    }
                }
            }
        }
    }
}