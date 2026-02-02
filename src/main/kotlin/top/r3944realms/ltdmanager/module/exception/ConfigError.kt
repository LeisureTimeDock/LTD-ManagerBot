package top.r3944realms.ltdmanager.module.exception

class ConfigError(type: Type = Type.OTHER, private val pos: String, vararg args: Any) : Exception() {
    private val errorType: Type = type
    private val arguments = args

    override val message: String
        get() = String.format(errorType.template, *arguments, pos)


    enum class Type(val template: String) {
        INVALID_PARAMETER("Invalid Parameter: %s in %s."),
        MISSING_PARAMETER("Missing Parameter: %s in %s."),
        NOT_EXPECTED_VALUE("Expect for %s but was %s in %s."),
        OTHER("%s in %s")
    }
}