package top.r3944realms.ltdmanager

import org.slf4j.LoggerFactory
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent
import top.r3944realms.ltdmanager.napcat.events.group.SetGroupPortraitEvent
import top.r3944realms.ltdmanager.napcat.events.group.SetGroupSearchEvent
import top.r3944realms.ltdmanager.napcat.events.personal.CanSendImageEvent
import top.r3944realms.ltdmanager.napcat.requests.account.SetOnlineStatusRequest

fun main() {
    val logger = LoggerFactory.getLogger("log")
    logger.info("Start")
    val toJSON = SetOnlineStatusRequest.ONLINE.toJSON()
    logger.info("S:{}",toJSON)
    val str: String = """
        {
            "status": "ok",
            "retcode": 0,
            "data": null,
            "message": "string",
            "wording": "string",
            "echo": "string"
        }
    """.trimIndent()
    val decodeEvent = NapCatEvent.decodeEvent(str, "group/set_group_search")
    if (decodeEvent is SetGroupSearchEvent) {
        logger.info("data:{}",decodeEvent.data)
    }
}