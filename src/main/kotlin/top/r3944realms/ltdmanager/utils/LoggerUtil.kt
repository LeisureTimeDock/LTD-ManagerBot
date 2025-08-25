package top.r3944realms.ltdmanager.utils

import org.slf4j.LoggerFactory

class LoggerUtil {
    companion object {
        val logger by lazy {
            LoggerFactory.getLogger("LTDManagerBot")
        }
    }
}