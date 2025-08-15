package top.r3944realms.ltdmanager.napcat.events.account

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

/**
 * QQ 账户相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
 */
@Serializable
abstract class AbstractAccountEvent(
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
    override fun type(): String {
        return "account/" + subtype()
    }

    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("account/set_qq_profile", SetQQProfileEvent.serializer())
                put("account/ArkSharePeer", GetArkSharePeerEvent.serializer())
                put("account/get_doubt_friends_add_request", GetDoubtFriendsAddRequestEvent.serializer())
                put("account/set_doubt_friends_add_request", SetDoubtFriendsAddRequestEvent.serializer())
                put("account/get_online_clients", GetOnlineClientsEvent.serializer())
                put("account/mark_msg_as_read", MarkMsgAsReadEvent.serializer())
                put("account/set_online_status", SetOnlineStatusEvent.serializer())
                put("account/ArkShareGroup", MarkMsgAsReadEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(GetArkSharePeerEvent::class)
                        subclass(SetQQProfileEvent::class)
                        subclass(GetDoubtFriendsAddRequestEvent::class)
                        subclass(SetDoubtFriendsAddRequestEvent::class)
                        subclass(GetOnlineClientsEvent::class)
                        subclass(MarkMsgAsReadEvent::class)
                    }
                }
            }
        }
    }
}