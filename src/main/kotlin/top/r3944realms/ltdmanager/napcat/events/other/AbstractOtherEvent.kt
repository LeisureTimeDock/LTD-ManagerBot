package top.r3944realms.ltdmanager.napcat.events.other

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent
/**
 * QQ 其它相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
 */
@Serializable
abstract class AbstractOtherEvent (
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
    override fun type(): String = "other/" + subtype()

    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("/send_private_msg",SendPrivateMsgEvent.serializer())
                put("/send_group_msg",SendGroupMsgEvent.serializer())
                put("/send_msg",SendMsgEvent.serializer())
                put("/unknown",UnknownEvent.serializer())
                put("/get_guild_list", GetGuildListEvent.serializer())
                put("/get_guild_service_profile", GetGuildServiceProfileEvent.serializer())
                put("/check_url_safely", CheckUrlSafelyEvent.serializer())
                put("/get_collection_list", GetCollectionListEvent.serializer())
                put("/get_group_ignore_add_request", GetGroupIgnoreAddRequestEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(SendPrivateMsgEvent::class)
                        subclass(SendGroupMsgEvent::class)
                        subclass(SendMsgEvent::class)
                        subclass(UnknownEvent::class)
                        subclass(GetGuildListEvent::class)
                        subclass(GetGuildServiceProfileEvent::class)
                        subclass(CheckUrlSafelyEvent::class)
                        subclass(GetCollectionListEvent::class)
                        subclass(GetGroupIgnoreAddRequestEvent::class)

                    }
                }
            }
        }
    }
}