package top.r3944realms.ltdmanager.napcat.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistoryContent
import top.r3944realms.ltdmanager.napcat.data.msghistory.MsgHistorySpecificMsg

object MsgHistoryContentSerializer : KSerializer<MsgHistoryContent> {

    // 创建宽松的 JSON 配置
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MsgHistoryContent")

    override fun serialize(encoder: Encoder, value: MsgHistoryContent) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("只能使用JSON编码器")

        when (value) {
            is MsgHistoryContent.SpecificMsgList -> {
                val jsonArray = JsonArray(value.value.map { specificMsg ->
                    json.encodeToJsonElement(specificMsg)
                })
                jsonEncoder.encodeJsonElement(jsonArray)
            }
            is MsgHistoryContent.StringValue -> {
                jsonEncoder.encodeJsonElement(JsonPrimitive(value.value))
            }
        }
    }

    override fun deserialize(decoder: Decoder): MsgHistoryContent {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("只能使用JSON解码器")

        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonArray -> {
                try {
                    val specificMsgList = jsonElement.map { element ->
                        json.decodeFromJsonElement<MsgHistorySpecificMsg>(element)
                    }
                    MsgHistoryContent.SpecificMsgList(specificMsgList)
                } catch (e: Exception) {
                    throw SerializationException("无法将JsonArray解析为List<MsgHistorySpecificMsg>: ${e.message}")
                }
            }
            is JsonPrimitive -> {
                if (jsonElement.isString) {
                    MsgHistoryContent.StringValue(jsonElement.content)
                } else {
                    throw SerializationException("不支持的非字符串原始类型")
                }
            }
            else -> {
                throw SerializationException("不支持的JSON元素类型: ${jsonElement::class.simpleName}")
            }
        }
    }
}