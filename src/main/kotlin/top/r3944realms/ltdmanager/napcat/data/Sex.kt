package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 性别
 */
@Serializable
enum class Sex(val value: String) {
    @SerialName("0") UNKNOWN("0"),
    @SerialName("1") MALE("1"),
    @SerialName("2") FEMALE("2");
}