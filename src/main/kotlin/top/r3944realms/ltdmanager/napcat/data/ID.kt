package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable

@Serializable
sealed class ID {
    class DoubleValue(val value: Double) : ID()
    class StringValue(val value: String) : ID()
}