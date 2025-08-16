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
    //TODO 太烦了 也许该考虑反射或代码生成
    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("account/set_qq_profile", SetQQProfileEvent.serializer())
                put("account/ArkSharePeer", ArkSharePeerEvent.serializer())
                put("account/get_doubt_friends_add_request", GetDoubtFriendsAddRequestEvent.serializer())
                put("account/set_doubt_friends_add_request", SetDoubtFriendsAddRequestEvent.serializer())
                put("account/get_online_clients", GetOnlineClientsEvent.serializer())
                put("account/mark_msg_as_read", MarkMsgAsReadEvent.serializer())
                put("account/set_online_status", SetOnlineStatusEvent.serializer())
                put("account/ArkShareGroup", MarkMsgAsReadEvent.serializer())
                put("account/get_friends_with_category",GetFriendsWithCategoryEvent.serializer())
                put("account/create_collection",CreateCollectionEvent.serializer())
                put("account/delete_friend",DeleteFriendEvent.serializer())
                put("account/fetch_custom_face",FetchCustomFaceEvent.serializer())
                put("account/get_friend_list",GetFriendListEvent.serializer())
                put("account/get_login_info",GetLoginInfoEvent.serializer())
                put("account/get_mini_app_ark",GetMiniAppArkEvent.serializer())
                put("account/_get_model_show",GetModelShowEvent.serializer())
                put("account/get_profile_like",GetProfileLikeEvent.serializer())
                put("account/get_recent_contact",GetRecentContactEvent.serializer())
                put("account/get_status",GetStatusEvent.serializer())
                put("account/get_stranger_info",GetStrangerInfoEvent.serializer())
                put("account/get_unidirectional_friend_list",GetUnidirectionalFriendListEvent.serializer())
                put("account/_mark_all_as_read",MarkAllAsReadEvent.serializer())
                put("account/mark_group_msg_as_read",MarkGroupMsgAsReadEvent.serializer())
                put("account/mark_private_msg_as_read",MarkPrivateMsgAsReadEvent.serializer())
                put("account/nc_get_user_status",NcGetUserStatusEvent.serializer())
                put("account/send_like",SendLikeEvent.serializer())
                put("account/set_diy_online_status",SetDiyOnlineStatusEvent.serializer())
                put("account/set_friend_add_request",SetFriendAddRequestEvent.serializer())
                put("account/set_friend_remark",SetFriendRemarkEvent.serializer())
                put("account/_set_model_show",SetModelShowEvent.serializer())
                put("account/set_qq_avatar",SetQqAvatarEvent.serializer())
                put("account/set_self_longnick",SetSelfLongnickEvent.serializer())

            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(SetQQProfileEvent::class)
                        subclass(ArkSharePeerEvent::class)
                        subclass(GetDoubtFriendsAddRequestEvent::class)
                        subclass(SetDoubtFriendsAddRequestEvent::class)
                        subclass(GetOnlineClientsEvent::class)
                        subclass(MarkMsgAsReadEvent::class)
                        subclass(SetOnlineStatusEvent::class)
                        subclass(ArkShareGroupEvent::class)
                        subclass(GetFriendsWithCategoryEvent::class)
                        subclass(CreateCollectionEvent::class)
                        subclass(DeleteFriendEvent::class)
                        subclass(FetchCustomFaceEvent::class)
                        subclass(GetFriendListEvent::class)
                        subclass(GetLoginInfoEvent::class)
                        subclass(GetMiniAppArkEvent::class)
                        subclass(GetModelShowEvent::class)
                        subclass(GetProfileLikeEvent::class)
                        subclass(GetRecentContactEvent::class)
                        subclass(GetStatusEvent::class)
                        subclass(GetStrangerInfoEvent::class)
                        subclass(GetUnidirectionalFriendListEvent::class)
                        subclass(MarkAllAsReadEvent::class)
                        subclass(MarkGroupMsgAsReadEvent::class)
                        subclass(MarkPrivateMsgAsReadEvent::class)
                        subclass(NcGetUserStatusEvent::class)
                        subclass(SendLikeEvent::class)
                        subclass(SetDiyOnlineStatusEvent::class)
                        subclass(SetFriendAddRequestEvent::class)
                        subclass(SetFriendRemarkEvent::class)
                        subclass(SetModelShowEvent::class)
                        subclass(SetQqAvatarEvent::class)
                        subclass(SetSelfLongnickEvent::class)
                    }
                }
            }
        }
    }
}