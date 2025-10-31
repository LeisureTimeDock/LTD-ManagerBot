package top.r394realms.ltdmanagertest.msg

import kotlinx.coroutines.delay
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.module.ModGroupHandlerModule
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.data.MessageType
import top.r3944realms.ltdmanager.napcat.request.message.SendForwardMsgRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendPrivateMsgRequest

fun main() = GlobalManager.runBlockingMain {
    val napCatClient = NapCatClient.create()
// formatAndSendForwardMessage(napCatClient, 2561098830L, "幸福亮亮")
    sendTestMsg(napCatClient)
}
private suspend fun sendTestMsg(napCatClient: NapCatClient) {
    val request = SendPrivateMsgRequest(listOf(MessageElement.image("https://pic.xiaobuawa.top/images/2025/09/30/icons8-postgresql-96d4af6da8d4bd8df5.png","图片")),ID.long(2561098830L))
    napCatClient.sendUnit(request)
}
private suspend fun formatAndSendForwardMessage(napCatClient: NapCatClient ,userId: Long, requesterNick: String) {
    // 虚拟数据 - 模拟有审核记录的情况
    val virtualRecord = ModGroupHandlerModule.RejectRecord(
        userId = userId,
        reason = mutableListOf(
            "模组作者是张三",
            "作者是李四",
            "制作人是王五",
            "我不知道",
            "可能是赵六吧"
        ),
        rejectCount = 5
    )

    // 虚拟数据 - 模拟无审核记录的情况（注释掉下面这行来测试）
    // val virtualRecord = null

    val record = virtualRecord
    val content = """
    📊 用户审核记录
    ──────────────────
    🔹 用户QQ号：${record.userId}
    🔹 尝试次数：${record.rejectCount}
    🔹 最终评分：${rate(record.rejectCount)} 
    
    📝 尝试答案：
    ${"\n" + record.reason.joinToString("\n") { "   • $it" }}
    
    ⚠️ 提示：请仔细阅读文档后再在群里提问，否则你会失去你的大脑🧠
    """.trimIndent()

    // 创建合并转发消息
    val forwardRequest = SendForwardMsgRequest(
        groupId = ID.long(920719236),
        messages = listOf(
            SendForwardMsgRequest.TopForwardMsg(
                data = SendForwardMsgRequest.MessageData(
                    content = listOf(
                        SendForwardMsgRequest.Message(
                            data = SendForwardMsgRequest.PurpleData(
                                text = content
                            ),
                            type = MessageType.Text
                        )
                    ),
                    nickname = "审核系统",
                    userId = ID.long(0) // 系统ID
                ),
                type = MessageType.Text
            )
        ),
        news = listOf(
            SendForwardMsgRequest.ForwardModelNews("用户审核记录详情")
        ),
        prompt = "📋 ${requesterNick}入群审核评分${rate(record.rejectCount ?: 0)}",
        source = "审核系统",
        summary = "点击查看用户 $requesterNick 的审核详情"
    )

    // 发送合并转发消息
    napCatClient.sendUnit(forwardRequest)
}

private fun rate(count: Int): String = when (count) {
    0 -> "SSS"
    1 -> "A"
    2 -> "B"
    3 -> "C"
    4 -> "D"
    else -> "F"
}