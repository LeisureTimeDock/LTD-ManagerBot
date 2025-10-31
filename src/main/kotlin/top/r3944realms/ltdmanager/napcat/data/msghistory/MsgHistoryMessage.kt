package top.r3944realms.ltdmanager.napcat.data.msghistory

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.data.MessageType

/**
 * 文本消息
 *
 * 艾特消息
 *
 * 表情消息
 *
 * 图片消息
 *
 * 文件消息
 *
 * 回复消息
 *
 * JSON消息
 *
 * 语音消息
 *
 * 视频消息
 *
 * markdown消息
 *
 * 消息forward
 */
@Serializable
data class MsgHistoryMessage (
    val data: MsgHistoryMessageData,
    val type: MessageType
)