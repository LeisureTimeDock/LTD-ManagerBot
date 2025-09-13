package top.r394realms.ltdmanagertest.msg

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
fun main() = GlobalManager.runBlockingMain {
    val napCatClient = NapCatClient.create()

    // 生成9x9乘法表字符串
    val multiplicationTable = buildString {
        for (i in 1..9) {
            for (j in 1..i) {
                append("$j×$i=${i * j}\t")
            }
            appendLine() // 换行
        }
    }

    // 生成对齐检查字符
    val alignmentCheck = buildString {
        appendLine("📏 对齐检查（每个数字占位）：")
        appendLine("1234567890") // 数字标尺
        appendLine("─".repeat(20)) // 分隔线

        for (i in 1..9) {
            for (j in 1..i) {
                val product = i * j
                val placeholder = "X".repeat("$j×$i=$product".length)
                append("$placeholder\t")
            }
            appendLine()
        }
    }

    napCatClient.sendUnit(
        SendGroupMsgRequest(
            listOf(
                MessageElement.at(ID.long(2561098830), "幸福亮亮"),
                MessageElement.text("\n"),
                MessageElement.text("9×9乘法表：\n"),
                MessageElement.text(multiplicationTable),
                MessageElement.text("\n────────────────────\n"),
                MessageElement.text(alignmentCheck),
                MessageElement.text("\n提问前，请看文档，不看文档就提问直接肘击（")
            ),
            ID.long(339340846)
        )
    )
}