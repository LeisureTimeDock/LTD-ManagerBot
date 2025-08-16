package top.r3944realms.ltdmanager.napcat.events.group

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

/**
 * QQ 群聊相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
 */
@Serializable
abstract class AbstractGroupEvent (
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
    override fun type(): String = "group/" + subtype()

    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("group/get_group_info",GetGroupInfoEvent.serializer())
                put("group/get_group_info_ex",GetGroupInfoExEvent.serializer())
                put("group/get_group_detail_info",GetGroupDetailInfoEvent.serializer())
                put("group/get_group_list",GetGroupListEvent.serializer())
                put("group/get_group_member_info",GetGroupMemberInfoEvent.serializer())
                put("group/get_group_member_list",GetGroupMemberListEvent.serializer())
                put("group/get_group_honor_info",GetGroupHonorInfoEvent.serializer())
                put("group/get_group_at_all_remain",GetGroupAtAllRemainEvent.serializer())
                put("group/get_group_shut_list",GetGroupShutListEvent.serializer())
                put("group/get_group_ignored_notifies",GetGroupIgnoredNotifiesEvent.serializer())
                put("group/get_group_system_msg",GetGroupSystemMsgEvent.serializer())
                put("group/get_essence_msg_list",GetEssenceMsgListEvent.serializer())
                put("group/set_group_name",SetGroupNameEvent.serializer())
                put("group/set_group_portrait",SetGroupPortraitEvent.serializer())
                put("group/set_group_search",SetGroupSearchEvent.serializer())
                put("group/set_group_add_option",SetGroupAddOptionEvent.serializer())
                put("group/set_group_robot_add_option",SetGroupRobotAddOptionEvent.serializer())
                put("group/set_group_remark",SetGroupRemarkEvent.serializer())
                put("group/set_group_card",SetGroupCardEvent.serializer())
                put("group/set_group_special_title",SetGroupSpecialTitleEvent.serializer())
                put("group/set_group_admin",SetGroupAdminEvent.serializer())
                put("group/set_group_leave",SetGroupLeaveEvent.serializer())
                put("group/set_essence_msg",SetEssenceMsgEvent.serializer())
                put("group/delete_essence_msg", DeleteEssenceMsgEvent.serializer())
                put("group/_send_group_notice",SendGroupNoticeEvent.serializer())
                put("group/_get_group_notice",GetGroupNoticeEvent.serializer())
                put("group/_del_group_notice",DelGroupNoticeEvent.serializer())
                put("group/set_group_kick",SetGroupKickEvent.serializer())
                put("group/set_group_kick_members",SetGroupKickMembersEvent.serializer())
                put("group/set_group_ban",SetGroupBanEvent.serializer())
                put("group/set_group_whole_ban",SetGroupWholeBanEvent.serializer())
                put("group/set_group_sign",SetGroupSignEvent.serializer())
                put("group/send_group_sign",SendGroupSignEvent.serializer())
                put("group/set_group_add_request",SetGroupAddRequestEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(GetGroupInfoEvent::class)
                        subclass(GetGroupInfoExEvent::class)
                        subclass(GetGroupDetailInfoEvent::class)
                        subclass(GetGroupListEvent::class)
                        subclass(GetGroupMemberInfoEvent::class)
                        subclass(GetGroupMemberListEvent::class)
                        subclass(GetGroupHonorInfoEvent::class)
                        subclass(GetGroupAtAllRemainEvent::class)
                        subclass(GetGroupShutListEvent::class)
                        subclass(GetGroupIgnoredNotifiesEvent::class)
                        subclass(GetGroupSystemMsgEvent::class)
                        subclass(GetEssenceMsgListEvent::class)
                        subclass(SetGroupNameEvent::class)
                        subclass(SetGroupPortraitEvent::class)
                        subclass(SetGroupSearchEvent::class)
                        subclass(SetGroupAddOptionEvent::class)
                        subclass(SetGroupRobotAddOptionEvent::class)
                        subclass(SetGroupRemarkEvent::class)
                        subclass(SetGroupCardEvent::class)
                        subclass(SetGroupSpecialTitleEvent::class)
                        subclass(SetGroupAdminEvent::class)
                        subclass(SetGroupLeaveEvent::class)
                        subclass(SetEssenceMsgEvent::class)
                        subclass(DeleteEssenceMsgEvent::class)
                        subclass(SendGroupNoticeEvent::class)
                        subclass(GetGroupNoticeEvent::class)
                        subclass(DelGroupNoticeEvent::class)
                        subclass(SetGroupKickEvent::class)
                        subclass(SetGroupKickMembersEvent::class)
                        subclass(SetGroupBanEvent::class)
                        subclass(SetGroupWholeBanEvent::class)
                        subclass(SetGroupSignEvent::class)
                        subclass(SendGroupSignEvent::class)
                        subclass(SetGroupAddRequestEvent::class)
                    }
                }
            }
        }
    }
}