package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable

@Serializable
sealed class QQ {
    class DoubleValue(val value: Double) : QQ()
    class StringValue(val value: String) : QQ()
}