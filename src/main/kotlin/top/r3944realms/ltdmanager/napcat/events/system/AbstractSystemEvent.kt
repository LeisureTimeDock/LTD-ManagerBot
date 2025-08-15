package top.r3944realms.ltdmanager.napcat.events.system

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import top.r3944realms.ltdmanager.napcat.events.NapCatEvent

abstract class AbstractSystemEvent
    : NapCatEvent() {
    override fun type(): String = "system/" + subtype()
    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {

            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {

                    }
                }
            }
        }
    }
}