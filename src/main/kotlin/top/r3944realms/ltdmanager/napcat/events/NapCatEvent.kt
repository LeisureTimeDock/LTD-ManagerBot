package top.r3944realms.ltdmanager.napcat.events

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.events.account.AbstractAccountEvent


/**
 * 基础NapCat事件类
 * @property httpStatusCode HTTP状态码
 * @property createTime 创建时间戳
 */
@Serializable
abstract class NapCatEvent(
    @Transient
    open val httpStatusCode: HttpStatus = HttpStatus.OK,
    @Transient
    open val createTime: Long = System.currentTimeMillis()
) {
    abstract fun type() :String
    abstract fun subtype(): String
    companion object {
        private val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                putAll(AbstractAccountEvent.eventTypeMap)
            }
        }


        fun decodeEvent(jsonString: String, type: String): NapCatEvent {
            return eventTypeMap[type]?.let { serializer ->
                // 如果是Account相关事件，使用AccountEvent的json配置
                if (type.startsWith("account/")) {
                    AbstractAccountEvent.json.decodeFromString(serializer, jsonString)
                } else {
                    // 其他类型的事件可以使用默认的Json配置
                    val json = Json {
                        ignoreUnknownKeys = true
                    }
                    json.decodeFromString(serializer, jsonString)
                }
            } ?: throw SerializationException("Unknown request type: $type")
        }
    }
    @Serializable
    enum class Status(val value: String) {
        @SerialName("ok") Ok("ok");
    }
    enum class HttpStatus(
        val code: Int,
        val message: String
    ) {
        // 1xx Informational
        CONTINUE(100, "Continue"),
        SWITCHING_PROTOCOLS(101, "Switching Protocols"),

        // 2xx Success
        OK(200, "OK"),
        CREATED(201, "Created"),

        // 3xx Redirection
        MOVED_PERMANENTLY(301, "Moved Permanently"),

        // 4xx Client Error
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),

        // 5xx Server Error
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable");
        companion object {
            private val values = entries.associateBy { it.code }
            fun fromCode(code: Int) = values[code] ?: throw IllegalArgumentException("无效的HTTP状态码: $code")
        }
    }
}