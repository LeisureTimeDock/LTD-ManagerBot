package top.r3944realms.ltdmanager.napcat.event.personal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

/**
 * QQ 个人相关响应抽象
 * @property status 状态字符串
 * @property retcode 返回代码
 * @property message 消息
 * @property wording 文字描述
 * @property echo 回显字段 (可空)
 */
@Serializable
abstract class AbstractPersonalEvent (
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
    override fun type(): String = "personal/" + subtype()

    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("personal/ocr_image", OcrImageEvent.serializer())
                put("personal/.ocr_image", PointOcrImageEvent.serializer())
                put("personal/translate_en2zh", TranslateEn2zhEvent.serializer())
                put("personal/.handle_quick_operation", PointHandleQuickOperationEvent.serializer())
                put("personal/can_send_image", CanSendImageEvent.serializer())
                put("personal/can_send_record", CanSendRecordEvent.serializer())
                put("personal/get_ai_characters", GetAiCharactersEvent.serializer())
                put("personal/click_inline_keyboard_button", ClickInlineKeyboardButtonEvent.serializer())
                put("personal/get_ai_record", GetAiRecordEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(OcrImageEvent::class)
                        subclass(PointOcrImageEvent::class)
                        subclass(TranslateEn2zhEvent::class)
                        subclass(PointHandleQuickOperationEvent::class)
                        subclass(CanSendImageEvent::class)
                        subclass(CanSendRecordEvent::class)
                        subclass(GetAiCharactersEvent::class)
                        subclass(ClickInlineKeyboardButtonEvent::class)
                        subclass(GetAiRecordEvent::class)
                    }
                }
            }
        }
    }
}