package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.module.Modules
import top.r3944realms.ltdmanager.module.exception.ConfigError

data class ModuleConfig(
    var modules: List<Module>? = emptyList()
) {
    data class Module(
        var name: String = "default",
        var type: ModuleType = ModuleType.HELP_MODULE,
        var enabled: Boolean = true,
        var dependencies: List<Dependency>? = emptyList(),
        var config: Map<String, Any> = emptyMap()
    ) {
        data class Dependency(
            var name: String = "unknown",
            var type: ModuleType = ModuleType.UNKNOWN_MODULE,
        ) {
            fun getDepName() :String = "${type.modName}-#$name"

        }

        fun findDependency(type: ModuleType): Dependency? {
            return dependencies?.find { it.type == type }
        }
        inline fun <reified T> typedList(paramName: String): List<T> {
            val list = anyList(paramName)
            return list.map { element ->
                when (T::class) {
                    String::class -> element.toString() as T
                    Int::class -> convertToInt(element, "$paramName.list.element") as T
                    Long::class -> convertToLong(element, "$paramName.list.element") as T
                    Boolean::class -> convertToBoolean(element, "$paramName.list.element") as T
                    else -> {
                        if (element is T) element
                        else throw ConfigError(
                            ConfigError.Type.NOT_EXPECTED_VALUE,
                            name,
                            "$paramName.list",
                            T::class.simpleName ?: T::class.java.simpleName,
                            element::class.simpleName ?: element::class.java.simpleName
                        )
                    }
                }
            }
        }
        // 特定类型的 List 方法
        fun <T> list(paramName: String): List<T> = get<List<T>>(paramName)

        // 特定类型的 Map 方法
        fun <K, V> map(paramName: String): Map<K, V> = get<Map<K, V>>(paramName)

        // 泛型 List 获取（返回 List<Any>）
        fun anyList(paramName: String): List<Any> = get<List<Any>>(paramName)

        // 泛型 Map 获取（返回 Map<String, Any>）
        fun anyMap(paramName: String): Map<String, Any> = get<Map<String, Any>>(paramName)

        // String List 的便捷方法
        fun stringList(paramName: String): List<String> {
            val list = anyList(paramName)
            return list.map { it.toString() }
        }

        // Int List 的便捷方法
        fun intList(paramName: String): List<Int> {
            val list = anyList(paramName)
            return list.map { value ->
                when (value) {
                    is Int -> value
                    is Number -> value.toInt()
                    is String -> value.toIntOrNull()
                        ?: throw ConfigError(
                            ConfigError.Type.NOT_EXPECTED_VALUE,
                            name,
                            paramName,
                            "List<Int>",
                            "元素类型: ${value::class.simpleName}"
                        )
                    else -> throw ConfigError(
                        ConfigError.Type.NOT_EXPECTED_VALUE,
                        name,
                        paramName,
                        "List<Int>",
                        "元素类型: ${value::class.simpleName}"
                    )
                }
            }
        }

        // 获取特定类型的 Map
        inline fun <reified V> typedMap(paramName: String): Map<String, V> {
            val map = anyMap(paramName)
            return map.mapValues { (key, value) ->
                when (V::class) {
                    String::class -> value.toString() as V
                    Int::class -> convertToInt(value, "$paramName.$key") as V
                    Long::class -> convertToLong(value, "$paramName.$key") as V
                    Boolean::class -> convertToBoolean(value, "$paramName.$key") as V
                    else -> {
                        if (value is V) value
                        else throw ConfigError(
                            ConfigError.Type.NOT_EXPECTED_VALUE,
                            name,
                            "$paramName.$key",
                            V::class.simpleName ?: V::class.java.simpleName,
                            value::class.simpleName ?: value::class.java.simpleName
                        )
                    }
                }
            }
        }

        enum class ModuleType(val modName: String) {
            GROUP_MESSAGE_POLLING_MODULE(Modules.GROUP_MESSAGE_POLLING),
            GROUP_REQUEST_HANDLER_MODULE(Modules.GROUP_REQUEST_HANDLER),
            MAIL_MODULE(Modules.MAIL),
            BAN_MODULE(Modules.BAN),
            DG_LAB_MODULE(Modules.DG_LAB),
            INVITE_MODULE(Modules.INVITATION_CODE),
            MC_SERVER_STATUS_MODULE(Modules.MC_SERVER_STATUS),
            MOD_GROUP_HANDLER_MODULE(Modules.MOD_GROUP_HANDLER),
            RCON_PLAYER_LIST_MODULE(Modules.RCON_PLAYER_LIST),
            STATE_MODULE(Modules.STATE),
            HELP_MODULE(Modules.HELP),
            UNKNOWN_MODULE("UnknownModule");
        }
        // 基础获取方法
        fun value(paramName: String): Any =
            config[paramName] ?: throw ConfigError(
                ConfigError.Type.MISSING_PARAMETER,
                name,
                paramName
            )

        // 泛型获取方法
        private inline fun <reified T> get(paramName: String): T {
            val value = value(paramName)
            return when (T::class) {
                Long::class -> convertToLong(value, paramName) as T
                Int::class -> convertToInt(value, paramName) as T
                String::class -> value.toString() as T
                Boolean::class -> convertToBoolean(value, paramName) as T
                Double::class -> convertToDouble(value, paramName) as T
                Float::class -> convertToFloat(value, paramName) as T
                else -> {
                    if (value is T) value
                    else throw typeMismatchError<T>(value, paramName)
                }
            }
        }

        // 特定类型方法（向后兼容）
        fun long(paramName: String): Long = get<Long>(paramName)
        fun int(paramName: String): Int = get<Int>(paramName)
        fun string(paramName: String): String = get<String>(paramName)
        fun boolean(paramName: String): Boolean = get<Boolean>(paramName)
        fun double(paramName: String): Double = get<Double>(paramName)
        fun float(paramName: String): Float = get<Float>(paramName)

        // 可选值方法
        inline fun <reified T> getOrNull(paramName: String): T? =
            config[paramName] as? T ?: run {
                val value = config[paramName]
                if (value == null) null
                else if (value is T) value
                else null
            }

        inline fun <reified T> getOrDefault(paramName: String, defaultValue: T): T =
            getOrNull<T>(paramName) ?: defaultValue

        // 类型转换辅助方法
        fun convertToLong(value: Any, paramName: String): Long = when (value) {
            is Long -> value
            is Number -> value.toLong()
            is String -> try {
                value.toLong()
            } catch (e: NumberFormatException) {
                throw typeMismatchError<Long>(value, paramName)
            }
            else -> throw typeMismatchError<Long>(value, paramName)
        }

        fun convertToInt(value: Any, paramName: String): Int = when (value) {
            is Int -> value
            is Number -> value.toInt()
            is String -> try {
                value.toInt()
            } catch (e: NumberFormatException) {
                throw typeMismatchError<Int>(value, paramName)
            }
            else -> throw typeMismatchError<Int>(value, paramName)
        }

        fun convertToBoolean(value: Any, paramName: String): Boolean = when (value) {
            is Boolean -> value
            is String -> when (value.lowercase()) {
                "true", "yes", "1" -> true
                "false", "no", "0" -> false
                else -> throw typeMismatchError<Boolean>(value, paramName)
            }
            is Number -> value.toInt() != 0
            else -> throw typeMismatchError<Boolean>(value, paramName)
        }

        private fun convertToDouble(value: Any, paramName: String): Double = when (value) {
            is Double -> value
            is Number -> value.toDouble()
            is String -> try {
                value.toDouble()
            } catch (e: NumberFormatException) {
                throw typeMismatchError<Double>(value, paramName)
            }
            else -> throw typeMismatchError<Double>(value, paramName)
        }

        private fun convertToFloat(value: Any, paramName: String): Float = when (value) {
            is Float -> value
            is Number -> value.toFloat()
            is String -> try {
                value.toFloat()
            } catch (e: NumberFormatException) {
                throw typeMismatchError<Float>(value, paramName)
            }
            else -> throw typeMismatchError<Float>(value, paramName)
        }

        // 错误处理辅助方法
        private inline fun <reified T> typeMismatchError(
            actualValue: Any,
            paramName: String
        ): Nothing {
            throw ConfigError(
                ConfigError.Type.NOT_EXPECTED_VALUE,
                paramName,
                T::class.simpleName ?: T::class.java.simpleName,
                actualValue::class.simpleName ?: actualValue::class.java.simpleName
            )
        }
    }
}


