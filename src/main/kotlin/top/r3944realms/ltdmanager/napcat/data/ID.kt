package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.serializer.IDSerializer

/**
 * ID
 */
@Serializable(with = IDSerializer::class)
sealed class ID {
    @Serializable
    class LongValue(val value: Long) : ID()
    @Serializable
    class StringValue(val value: String) : ID()
    companion object {
        fun long(value: Long) = LongValue(value)
        fun str(value: String) = StringValue(value)
    }
}