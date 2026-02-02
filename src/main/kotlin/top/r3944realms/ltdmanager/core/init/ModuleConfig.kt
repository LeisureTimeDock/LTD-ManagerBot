package top.r3944realms.ltdmanager.core.init

import top.r3944realms.ltdmanager.module.Modules
import top.r3944realms.ltdmanager.module.exception.ConfigError

data class ModuleConfig(
    val name: String,
    val type: ModuleType,
    val enabled: Boolean,
    val dependencies: List<Dependency> = emptyList(),
    val config: Map<String, Any> = emptyMap()
) {
    data class Dependency(
        private val name: String,
        val type: ModuleType,
        val required: Boolean = true
    ) {
        private val dependencyName: String = "${type.modName}-$name"

        fun getDepName() :String = dependencyName

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
        HELP_MODULE(Modules.HELP),;
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
    private fun convertToLong(value: Any, paramName: String): Long = when (value) {
        is Long -> value
        is Number -> value.toLong()
        is String -> try {
            value.toLong()
        } catch (e: NumberFormatException) {
            throw typeMismatchError<Long>(value, paramName)
        }
        else -> throw typeMismatchError<Long>(value, paramName)
    }

    private fun convertToInt(value: Any, paramName: String): Int = when (value) {
        is Int -> value
        is Number -> value.toInt()
        is String -> try {
            value.toInt()
        } catch (e: NumberFormatException) {
            throw typeMismatchError<Int>(value, paramName)
        }
        else -> throw typeMismatchError<Int>(value, paramName)
    }

    private fun convertToBoolean(value: Any, paramName: String): Boolean = when (value) {
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
            name,
            T::class.simpleName ?: T::class.java.simpleName,
            actualValue::class.simpleName ?: actualValue::class.java.simpleName
        )
    }
}
