package top.r3944realms.ltdmanager.module.common

/**
 * 高级命令解析器
 * 支持自定义参数语法和参数验证
 */
class AdvancedCommandParser {
    private val commands = mutableListOf<CommandDefinition>()

    /**
     * 命令定义类
     */
    data class CommandDefinition(
        val name: String,
        val aliases: List<String> = emptyList(),
        val syntax: String = "",
        val description: String = "",
        val parameterPattern: Regex = DEFAULT_PARAMETER_PATTERN
    ) {
        val allCommandForms: List<String> get() = listOf(name) + aliases
    }

    /**
     * 解析结果
     */
    data class ParseResult(
        val command: String,
        val arguments: Map<String, String> = emptyMap(),
        val rawArguments: List<String> = emptyList(),
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val commandDefinition: CommandDefinition? = null
    )

    companion object {
        // 默认参数模式：<参数名> 或 [可选参数名]
        val DEFAULT_PARAMETER_PATTERN = Regex("""<(\w+)>|\[(\w+)]""")

        // 常用参数模式
        /**
         * 必需参数
         */
        val ANGLE_BRACKETS = Regex("""<(\w+)>""") // <param>

        /**
         * 可选参数
         */
        val SQUARE_BRACKETS = Regex("""\[(\w+)]""") // [param]

        /**
         * 自定义参数类型
         */
        val CURLY_BRACES = Regex("""\{(\w+)}""") // {param}
    }

    /**
     * 注册命令
     */
    fun registerCommand(
        name: String,
        aliases: List<String> = emptyList(),
        syntax: String = "",
        description: String = "",
        parameterPattern: Regex = DEFAULT_PARAMETER_PATTERN
    ): AdvancedCommandParser {
        commands.add(CommandDefinition(name, aliases, syntax, description, parameterPattern))
        return this
    }

    /**
     * 批量注册命令
     */
    fun registerCommands(vararg commandDefs: CommandDefinition): AdvancedCommandParser {
        commands.addAll(commandDefs)
        return this
    }
    /**
     * 智能分割参数，正确处理引号内的空格
     */
    private fun smartSplit(input: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var quoteChar: Char? = null
        var escapeNext = false

        for (char in input) {
            when {
                escapeNext -> {
                    current.append(char)
                    escapeNext = false
                }
                char == '\\' -> {
                    escapeNext = true
                }
                char == '"' || char == '\'' -> {
                    if (inQuotes && char == quoteChar) {
                        // 结束引号
                        inQuotes = false
                        quoteChar = null
                    } else if (!inQuotes) {
                        // 开始引号
                        inQuotes = true
                        quoteChar = char
                    } else {
                        current.append(char)
                    }
                }
                char == ' ' && !inQuotes -> {
                    // 空格分隔，但不是引号内
                    if (current.isNotEmpty()) {
                        result.add(current.toString())
                        current.clear()
                    }
                }
                else -> {
                    current.append(char)
                }
            }
        }

        if (current.isNotEmpty()) {
            result.add(current.toString())
        }

        return result
    }
    /**
     * 解析命令
     */
    private fun parse(input: String): ParseResult {
        val trimmedInput = input.trim()
        if (trimmedInput.isEmpty()) {
            return ParseResult("", isValid = false, errorMessage = "输入为空")
        }

        // 分割命令和参数
        val parts = smartSplit(trimmedInput)
        val commandPart = parts[0]

        // 查找匹配的命令定义
        val commandDef = commands.find { def ->
            def.allCommandForms.any { it.equals(commandPart, ignoreCase = true) }
        }

        if (commandDef == null) {
            return ParseResult(
                commandPart,
                isValid = false,
                errorMessage = "未知命令: $commandPart"
            )
        }

        // 解析参数
        val arguments = parseArguments(commandDef, parts.drop(1))
        val rawArgs = parts.drop(1)

        return ParseResult(
            command = commandDef.name,
            arguments = arguments,
            rawArguments = rawArgs,
            commandDefinition = commandDef
        )
    }

    /**
     * 解析参数
     */
    private fun parseArguments(commandDef: CommandDefinition, args: List<String>): Map<String, String> {
        val parameters = extractParameterNames(commandDef.syntax, commandDef.parameterPattern)
        val result = mutableMapOf<String, String>()

        if (parameters.isEmpty()) {
            args.forEachIndexed { index, value -> result["arg${index + 1}"] = value }
            return result
        }

        val positionals = mutableListOf<String>()
        val namedParams = mutableMapOf<String, String>()
        var i = 0

        // 第一遍：处理命名参数
        while (i < args.size) {
            when {
                args[i].startsWith("--") -> {
                    val paramName = args[i].substring(2)
                    if (paramName in parameters) {
                        if (i + 1 < args.size && !args[i + 1].startsWith("-")) {
                            namedParams[paramName] = args[i + 1]
                            i += 2
                        } else {
                            namedParams[paramName] = "true"
                            i += 1
                        }
                    } else {
                        positionals.add(args[i])
                        i += 1
                    }
                }
                args[i].startsWith("-") && args[i].length > 1 && !args[i].startsWith("--") -> {
                    val paramName = args[i].substring(1)
                    if (paramName in parameters) {
                        if (i + 1 < args.size && !args[i + 1].startsWith("-")) {
                            namedParams[paramName] = args[i + 1]
                            i += 2
                        } else {
                            namedParams[paramName] = "true"
                            i += 1
                        }
                    } else {
                        positionals.add(args[i])
                        i += 1
                    }
                }
                else -> {
                    positionals.add(args[i])
                    i += 1
                }
            }
        }

        // 第二遍：映射位置参数
        var posIndex = 0
        for (paramName in parameters) {
            if (paramName !in namedParams && posIndex < positionals.size) {
                result[paramName] = positionals[posIndex]
                posIndex++
            } else if (paramName in namedParams) {
                result[paramName] = namedParams[paramName]!!
            }
        }

        // 处理额外参数
        for (j in posIndex until positionals.size) {
            result["extraArg${j - posIndex + 1}"] = positionals[j]
        }

        return result
    }

    /**
     * 从语法字符串中提取参数名
     */
    private fun extractParameterNames(syntax: String, pattern: Regex): List<String> {
        if (syntax.isEmpty()) return emptyList()

        return pattern.findAll(syntax).map { matchResult ->
            matchResult.groupValues[1].ifEmpty { matchResult.groupValues[2] }
        }.toList()
    }

    /**
     * 验证参数是否符合要求
     */
    fun validateArguments(result: ParseResult): ParseResult {
        if (!result.isValid) return result

        val commandDef = result.commandDefinition ?: return result.copy(
            isValid = false,
            errorMessage = "命令定义不存在"
        )

        val requiredParams = extractParameterNames(commandDef.syntax, ANGLE_BRACKETS)
        val missingParams = requiredParams.filter { it !in result.arguments }

        return if (missingParams.isNotEmpty()) {
            result.copy(
                isValid = false,
                errorMessage = "缺少必需参数: ${missingParams.joinToString()}"
            )
        } else {
            result
        }
    }

    /**
     * 获取命令的帮助信息（增强版）
     */
    fun getCommandHelp(commandName: String): String? {
        val commandDef = commands.find { it.name == commandName || commandName in it.aliases }
        return commandDef?.let { def ->
            buildString {
                appendLine("命令: ${def.name}")
                if (def.aliases.isNotEmpty()) {
                    appendLine("别名: ${def.aliases.joinToString()}")
                }
                appendLine("用法: ${def.name} ${def.syntax}")
                appendLine("描述: ${def.description}")

                // 显示参数说明
                val params = extractParameterNames(def.syntax, def.parameterPattern)
                if (params.isNotEmpty()) {
                    appendLine("参数:")
                    params.forEach { param ->
                        val isRequired = def.syntax.contains("<$param>")
                        appendLine("  ${if (isRequired) "<$param>" else "[$param]"} - ${if (isRequired) "必需" else "可选"}")
                    }
                }
            }
        }
    }
    /**
     * 获取所有注册的命令
     */
    fun getRegisteredCommands(): List<CommandDefinition> = commands.toList()
    /**
     * 获取所有命令的帮助信息
     */
    fun getAllCommandsHelp(): String {
        return buildString {
            appendLine("可用命令:")
            appendLine("=".repeat(10))
            commands.forEach { def ->
                appendLine("${def.name} - ${def.description}")
                if (def.aliases.isNotEmpty()) {
                    appendLine("  别名: ${def.aliases.joinToString()}")
                }
                appendLine("  用法: ${def.name} ${def.syntax}")
                appendLine()
            }
        }
    }
    /**
     * 检查输入是否包含有效命令
     */
    fun containsCommand(input: String): Boolean {
        val trimmedInput = input.trim()
        if (trimmedInput.isEmpty()) return false

        val commandPart = trimmedInput.split("\\s+".toRegex())[0]
        return commands.any { def ->
            def.allCommandForms.any { it.equals(commandPart, ignoreCase = true) }
        }
    }

    /**
     * 快速解析（包含验证）
     */
    fun parseAndValidate(input: String): ParseResult {
        return validateArguments(parse(input))
    }
}
