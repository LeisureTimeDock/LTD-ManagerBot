package top.r3944realms.ltdmanager.dglab.model.pulseware

import kotlinx.serialization.Serializable

@Serializable
data class PulseWaveListSerializable(
    var name: String = "",
    val list: MutableList<PulseWaveSerializable> = mutableListOf()
)