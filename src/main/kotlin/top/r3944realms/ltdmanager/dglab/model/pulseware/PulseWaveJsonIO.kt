package top.r3944realms.ltdmanager.dglab.model.pulseware

import com.r3944realms.dg_lab.api.message.data.PulseWaveList
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.dglab.model.pulseware.CustomPulseDataConverter.toPulseWaveList
import top.r3944realms.ltdmanager.dglab.model.pulseware.CustomPulseDataConverter.toSerializable
import java.io.File

object PulseWaveJsonIO {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun saveToFile(map: Map<String, PulseWaveList>, file: File) {
        val serializableMap = map.mapValues { it.value.toSerializable() }
        file.writeText(json.encodeToString(serializableMap))
    }

    fun loadFromFile(file: File): Map<String, PulseWaveList> {
        if (!file.exists()) return emptyMap()
        val type = MapSerializer(String.serializer(), PulseWaveListSerializable.serializer())
        val data: Map<String, PulseWaveListSerializable> = json.decodeFromString(type, file.readText())
        return data.mapValues { it.value.toPulseWaveList() }
    }
}