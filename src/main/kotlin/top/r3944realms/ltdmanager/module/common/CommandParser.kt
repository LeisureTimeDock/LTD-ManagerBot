package top.r3944realms.ltdmanager.module.util

/**
 * 命令解析器
 * 严格模式：只支持命令后带空格的情况，避免误读
 */
class CommandParser(private val commands: List<String>) {

    /**
     * 解析命令
     * @param text 输入的文本
     * @return Pair<命令, 参数> 或 null（如果不是有效命令）
     */
    fun parseCommand(text: String): Pair<String, String>? {
        val trimmedText = text.trim()

        // 查找匹配的命令（必须后面跟着空格或字符串结束）
        val matchedCommand = commands.firstOrNull { command ->
            trimmedText.startsWith("$command ") || trimmedText == command
        } ?: return null

        // 获取参数部分
        val argument = if (trimmedText.length > matchedCommand.length) {
            trimmedText.substring(matchedCommand.length).trim()
        } else {
            ""
        }

        return Pair(matchedCommand, argument)
    }

    /**
     * 检查文本是否包含有效命令
     */
    fun containsCommand(text: String): Boolean {
        return parseCommand(text.trim()) != null
    }

    /**
     * 获取命令部分（不包含参数）
     */
    fun getCommandOnly(text: String): String? {
        return parseCommand(text.trim())?.first
    }

    /**
     * 获取参数部分（不包含命令）
     */
    fun getArgumentOnly(text: String): String {
        return parseCommand(text.trim())?.second ?: ""
    }
}