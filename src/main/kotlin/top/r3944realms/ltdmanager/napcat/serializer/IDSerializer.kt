package top.r3944realms.ltdmanager.napcat.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import top.r3944realms.ltdmanager.napcat.data.ID

object IDSerializer  : KSerializer<ID> {
    // 序列化时安全输出为字符串
    override fun serialize(encoder: Encoder, value: ID) {
        when (value) {
            is ID.StringValue -> encoder.encodeString(value.value)
            is ID.LongValue -> encoder.encodeString(value.value.toString()) // 改为 encodeString
        }
    }

    // 反序列化统一为 StringValue，保证大数字安全
    override fun deserialize(decoder: Decoder): ID {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            when {
                element.jsonPrimitive.isString -> ID.StringValue(element.jsonPrimitive.content)
                element.jsonPrimitive.longOrNull != null -> ID.StringValue(element.jsonPrimitive.long.toString())
                else -> throw IllegalArgumentException("无法解析ID: $element")
            }
        } else {
            ID.StringValue(decoder.decodeString())
        }
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ID", PrimitiveKind.STRING)
}