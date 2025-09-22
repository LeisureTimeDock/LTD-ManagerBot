package top.r3944realms.ltdmanager.dglab.model.pulseware

import kotlinx.serialization.Serializable

@Serializable
data class PulseWaveSerializable(
    val f1: Int, val f2: Int, val f3: Int, val f4: Int,
    val s1: Int, val s2: Int, val s3: Int, val s4: Int
)