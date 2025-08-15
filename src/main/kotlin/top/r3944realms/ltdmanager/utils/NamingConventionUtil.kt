package top.r3944realms.ltdmanager.utils

object NamingConventionUtil {
    fun camelToHyphen(str: String): String {
        return str.replace(Regex("([a-z0-9])([A-Z])"), "$1-$2").lowercase()
    }
    fun hyphenToCamel(name: String): String {
        val result = StringBuilder()
        var nextUpper = false

        for (c in name) {
            when {
                c == '-' -> nextUpper = true
                nextUpper -> {
                    result.append(c.uppercaseChar())
                    nextUpper = false
                }
                else -> result.append(c)
            }
        }

        return result.toString()
    }
}