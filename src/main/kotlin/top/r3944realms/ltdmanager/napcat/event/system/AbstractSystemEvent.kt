package top.r3944realms.ltdmanager.napcat.event.system

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import top.r3944realms.ltdmanager.napcat.event.NapCatEvent

abstract class AbstractSystemEvent
    : NapCatEvent() {
    override fun type(): String = "system/" + subtype()
    companion object {
        val eventTypeMap by lazy {
            mutableMapOf<String, KSerializer<out NapCatEvent>>().apply {
                put("system/get_robot_uin_range", GetRobotUinRangeEvent.serializer())
                put("system/bot_exit",BotExitEvent.serializer())
                put("system/send_packet", SendPacketEvent.serializer())
                put("system/nc_get_packet_status", NcGetPacketStatusEvent.serializer())
                put("system/get_version_info", GetVersionInfoEvent.serializer())
            }
        }
        internal val json: Json by lazy {
            Json {
                ignoreUnknownKeys = true
                serializersModule = SerializersModule {
                    polymorphic(NapCatEvent::class) {
                        subclass(GetRobotUinRangeEvent::class)
                        subclass(BotExitEvent::class)
                        subclass(SendPacketEvent::class)
                        subclass(NcGetPacketStatusEvent::class)
                        subclass(GetVersionInfoEvent::class)
                    }
                }
            }
        }
    }
}