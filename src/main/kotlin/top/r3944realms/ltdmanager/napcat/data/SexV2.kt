package top.r3944realms.ltdmanager.napcat.data;

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 回应格式不统一导致有了第二版本
 */
@Serializable
enum class SexV2(val value: String) {
    @SerialName("female") FEMALE("female"),
    @SerialName("male") MALE("male"),
    @SerialName("unknown") UNKNOWN("unknown");
}
