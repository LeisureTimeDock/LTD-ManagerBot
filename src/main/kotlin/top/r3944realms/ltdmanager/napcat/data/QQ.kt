package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable

@Serializable
sealed class Qq {
    class DoubleValue(val value: Double) : Qq()
    class StringValue(val value: String) : Qq()
}