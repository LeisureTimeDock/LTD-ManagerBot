package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Role(val value: String) {
    @SerialName("admin") Admin("admin"),
    @SerialName("member") Member("member"),
    @SerialName("owner") Owner("owner");
}