package top.r3944realms.ltdmanager

import org.slf4j.LoggerFactory
import top.r3944realms.ltdmanager.napcat.data.Sex
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent
import top.r3944realms.ltdmanager.napcat.events.account.SetQQProfileEvent
import top.r3944realms.ltdmanager.napcat.requests.account.SetQQProfileRequest

fun main() {
    val logger = LoggerFactory.getLogger("log")
    logger.info("Start")

    // 创建请求
    val request = SetQQProfileRequest(
        nickname = "123",
        personalNote = "232",
        sex = Sex.FEMALE
    )

    // 序列化（会自动添加type字段）
    val jsonStr = request.toJSON()
    logger.info("Serialized: {}", jsonStr)
    // 输出示例: {"type":"account/setQQProfile","nickname":"123","personal_note":"232","sex":"2"}
    val decodeJson =
        """
        {
            "status": "ok",
            "retcode": 0,
            "data": {
                "result": 0,
                "errMsg": "string"
            },
            "message": "string",
            "wording": "string",
            "echo": "string"
        }
    """.trimIndent();
    try {
        when (val decoded = NapCatEvent.decodeEvent(decodeJson, request.type())) {
            is SetQQProfileEvent -> {
                println("""
                    反序列化成功:
                    {
                        "status": ${decoded.status},
                        "retcode": ${decoded.retcode},
                        "data": {
                            "result": ${decoded.data.result},
                            "errMsg": ${decoded.data.errorMsg}
                        },
                        "message": ${decoded.message},
                        "wording": ${decoded.wording},
                        "echo": ${decoded.echo}
                    }
                """.trimIndent())
            }
            else -> println("未知请求类型")
        }
    } catch (e: Exception) {
        println("反序列化失败: ${e.message}")
    }
}