package top.r3944realms.ltdmanager.napcat.event.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent
import top.r3944realms.ltdmanager.napcat.event.message.group.ForwardGroupSingleMsgEvent
import top.r3944realms.ltdmanager.napcat.event.message.group.GroupPokeEvent
import top.r3944realms.ltdmanager.napcat.event.message.group.SendGroupForwardMsgEvent
import top.r3944realms.ltdmanager.napcat.event.message.personal.ForwardFriendSingleMsgEvent
import top.r3944realms.ltdmanager.napcat.event.message.personal.FriendPokeEvent
import top.r3944realms.ltdmanager.napcat.event.message.personal.SendPrivateForwardMsgEvent
import top.r3944realms.ltdmanager.napcat.event.other.SendGroupMsgEvent
import top.r3944realms.ltdmanager.napcat.event.other.SendPrivateMsgEvent

/**
 * QQ 消息相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
 */
@Serializable
abstract class AbstractMessageEvent (
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
    override fun type(): String = "message/" + subtype()

    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("message/send_group_msg", SendGroupMsgEvent.serializer())
                put("message/send_group_forward_msg", SendGroupForwardMsgEvent.serializer())
                put("message/forward_group_single_msg", ForwardGroupSingleMsgEvent.serializer())
                put("message/group_poke", GroupPokeEvent.serializer())
                put("message/send_private_msg", SendPrivateMsgEvent.serializer())
                put("message/send_private_forward_msg", SendPrivateForwardMsgEvent.serializer())
                put("message/forward_friend_single_msg", ForwardFriendSingleMsgEvent.serializer())
                put("message/friend_poke", FriendPokeEvent.serializer())
                put("message/send_poke", SendPokeEvent.serializer())
                put("message/delete_msg", DeleteMsgEvent.serializer())
                put("message/get_group_msg_history", GetGroupMsgHistoryEvent.serializer())
                put("message/get_friend_msg_history", GetFriendMsgHistoryEvent.serializer())
                put("message/get_msg", GetMsgEvent.serializer())
                put("message/get_forward_msg", GetForwardMsgEvent.serializer())
                put("message/send_forward_msg", SendForwardMsgEvent.serializer())
                put("message/set_msg_emoji_like", SetMsgEmojiLikeEvent.serializer())
                put("message/fetch_emoji_like", FetchEmojiLikeEvent.serializer())
                put("message/get_record", GetRecordEvent.serializer())
                put("message/get_image", GetImageEvent.serializer())
                put("message/send_group_ai_record", SendGroupAiRecordEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(SendGroupMsgEvent::class)
                        subclass(SendGroupForwardMsgEvent::class)
                        subclass(ForwardGroupSingleMsgEvent::class)
                        subclass(GroupPokeEvent::class)
                        subclass(SendPrivateMsgEvent::class)
                        subclass(SendPrivateForwardMsgEvent::class)
                        subclass(ForwardFriendSingleMsgEvent::class)
                        subclass(FriendPokeEvent::class)
                        subclass(SendPokeEvent::class)
                        subclass(DeleteMsgEvent::class)
                        subclass(GetGroupMsgHistoryEvent::class)
                        subclass(GetFriendMsgHistoryEvent::class)
                        subclass(GetMsgEvent::class)
                        subclass(GetForwardMsgEvent::class)
                        subclass(SendForwardMsgEvent::class)
                        subclass(SetMsgEmojiLikeEvent::class)
                        subclass(FetchEmojiLikeEvent::class)
                        subclass(GetRecordEvent::class)
                        subclass(GetImageEvent::class)
                        subclass(SendGroupAiRecordEvent::class)
                    }
                }
            }
        }
    }
}