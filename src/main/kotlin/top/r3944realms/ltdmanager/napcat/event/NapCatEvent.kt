package top.r3944realms.ltdmanager.napcat.event

import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.event.account.AbstractAccountEvent
import top.r3944realms.ltdmanager.napcat.event.file.AbstractFileEvent
import top.r3944realms.ltdmanager.napcat.event.group.AbstractGroupEvent
import top.r3944realms.ltdmanager.napcat.event.message.AbstractMessageEvent
import top.r3944realms.ltdmanager.napcat.event.other.AbstractOtherEvent
import top.r3944realms.ltdmanager.napcat.event.passkey.AbstractPassKeyEvent
import top.r3944realms.ltdmanager.napcat.event.personal.AbstractPersonalEvent
import top.r3944realms.ltdmanager.napcat.event.system.AbstractSystemEvent


/**
 * 基础NapCat事件类
 * @property httpStatusCode HTTP状态码
 * @property createTime 创建时间戳
 */
@Serializable
abstract class NapCatEvent(
    @Transient
    open val httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
    @Transient
    open val createTime: Long = System.currentTimeMillis()
) {
    abstract fun type() :String
    abstract fun subtype(): String
    companion object {
        private val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                putAll(AbstractAccountEvent.eventTypeMap)
                putAll(AbstractFileEvent.eventTypeMap)
                putAll(AbstractOtherEvent.eventTypeMap)
                putAll(AbstractPersonalEvent.eventTypeMap)
                putAll(AbstractPassKeyEvent.eventTypeMap)
                putAll(AbstractGroupEvent.eventTypeMap)
                putAll(AbstractSystemEvent.eventTypeMap)
                putAll(AbstractMessageEvent.eventTypeMap)
            }
        }

        private fun failedDecode(jsonString: String): FailedRequestEvent {
            return FailedRequestEvent.json.decodeFromString(jsonString)
        }
        fun decodeEvent(jsonString: String, type: String): NapCatEvent {
            return try {
                eventTypeMap[type]?.let { serializer ->
                    val json = when {
                        type.startsWith("account/") -> AbstractAccountEvent.json
                        type.startsWith("file/") -> AbstractFileEvent.json
                        type.startsWith("group/") -> AbstractGroupEvent.json
                        type.startsWith("message/") -> AbstractMessageEvent.json
                        type.startsWith("passkey/") -> AbstractPassKeyEvent.json
                        type.startsWith("personal/") -> AbstractPersonalEvent.json
                        type.startsWith("system/") -> AbstractSystemEvent.json
                        type.startsWith("other/") -> AbstractOtherEvent.json
                        else -> Json { ignoreUnknownKeys = true }
                    }
                    json.decodeFromString(serializer, jsonString)
                } ?: failedDecode(jsonString) // 找不到类型，直接 fallback
            } catch (e: Exception) {
                // 解码失败，fallback
                failedDecode(jsonString)
            }
        }
    }
    open fun isOk():Boolean = true
    @Serializable
    enum class Status(val value: String) {
        @SerialName("ok") Ok("ok"),
        @SerialName("failed") Failed("failed"),;
        companion object {
            fun isOk(value: Status): Boolean = value == Ok
        }
    }
}