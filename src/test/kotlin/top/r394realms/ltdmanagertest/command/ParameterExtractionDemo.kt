package top.r394realms.ltdmanagertest.command

import top.r3944realms.ltdmanager.module.common.AdvancedCommandParser

/**
 * 参数提取演示类
 */
class ParameterExtractionDemo {

    companion object {
        // 默认参数模式：<参数名> 或 [可选参数名]
        val DEFAULT_PARAMETER_PATTERN = Regex("""<(\w+)>|\[(\w+)]""")

        // 常用参数模式
        val ANGLE_BRACKETS = Regex("""<(\w+)>""") // <param> - 必需参数
        val SQUARE_BRACKETS = Regex("""\[(\w+)]""") // [param] - 可选参数
        val CURLY_BRACES = Regex("""\{(\w+)}""") // {param} - 自定义参数
    }

    /**
     * 从语法字符串中提取参数名
     */
    fun extractParameterNames(syntax: String, pattern: Regex): List<String> {
        if (syntax.isEmpty()) return emptyList()

        return pattern.findAll(syntax).map { matchResult ->
            // 从捕获组中提取参数名（处理不同的括号类型）
            matchResult.groupValues[1].ifEmpty { matchResult.groupValues[2] }
        }.toList()
    }

    /**
     * 演示场景1：只需要必需参数（参数验证）
     */
    fun demoRequiredParameters() {
        println("=== 场景1：必需参数验证 ===")

        val syntax = "send <message> [target] [priority]"

        val parser = AdvancedCommandParser().apply {
            registerCommand(
                "ls",
                syntax = syntax,
                parameterPattern = ANGLE_BRACKETS,
            )
        }
        println(parser.getCommandHelp("ls"))

        // 模拟用户输入验证
        val testCases = listOf(
            "ls send Hello",					// 有效：提供了必需参数
            "ls send Hello @all",				// 有效：提供了必需参数和可选参数
            "ls send"							// 无效：缺少必需参数
        )

        testCases.forEach { input ->
            println("输入: $input")
            val result = parser.parseAndValidate(input)
            if (result.isValid) {
                println("✓ 命令: ${result.command}")
                println("✓ 参数:")
                result.arguments.forEach { (key, value) ->
                    println("  $key: $value")
                }
            } else {
                println("✗ 错误: ${result.errorMessage}")
            }
            println("-".repeat(50))
        }
        println()
    }

    /**
     * 演示场景2：需要所有参数（完整解析）
     */
    fun demoAllParameters() {
        println("=== 场景2：完整参数解析 ===")

        val syntax = "user <action> <id> [name] [age] [email]"
        val parser = AdvancedCommandParser().apply {
            registerCommand(
                "ls",
                syntax = syntax,
            )
        }
        println(parser.getCommandHelp("ls"))

        // 模拟参数映射
        val testInput = "ls user add 123 John 30 john@example.com"

        println("输入: $testInput")
        val result = parser.parseAndValidate(testInput)
        if (result.isValid) {
            println("✓ 命令: ${result.command}")
            println("✓ 参数:")
            result.arguments.forEach { (key, value) ->
                println("  $key: $value")
            }
        } else {
            println("✗ 错误: ${result.errorMessage}")
        }
        println("-".repeat(50))
    }

    /**
     * 演示场景3：自定义参数格式
     */
    fun demoCustomParameters() {
        println("=== 场景3：自定义参数格式 ===")

        val customSyntax = "execute {command} {args} --timeout {timeout} --retry {retries}"
        extractParameterNames(customSyntax, CURLY_BRACES)
        val parser = AdvancedCommandParser().apply {
            registerCommand(
                "ls",
                syntax = customSyntax,
                parameterPattern = CURLY_BRACES,
            )
        }
       println(parser.getCommandHelp("ls"))

        // 模拟命名参数解析
        val testInput = "ls execute {ls -la} {--help} --timeout 30 --retry 3"
        val result = parser.parseAndValidate(testInput)
        if (result.isValid) {
            println("✓ 命令: ${result.command}")
            println("✓ 参数:")
            result.arguments.forEach { (key, value) ->
                println("  $key: $value")
            }
        } else {
            println("✗ 错误: ${result.errorMessage}")
        }
        println("-".repeat(5))

    }

    /**
     * 综合演示：完整的命令处理流程
     */
    fun demoCompleteWorkflow() {
        println("=== 综合演示：完整工作流程 ===")

        // 定义复杂的命令语法
        val syntax = "ls database <operation> <table> [where] [limit] [offset] --format {format}"
        val parser1 = AdvancedCommandParser().apply {
            registerCommand(
                "ls",
                syntax = syntax,
                parameterPattern = DEFAULT_PARAMETER_PATTERN,
            )
        }

        // 模拟真实命令处理
        val testCommand = "ls database select users --where \"age > 18\" --limit 10 --format json"
        val result = parser1.parseAndValidate(testCommand)
        if (result.isValid) {
            println("✓ 命令: ${result.command}")
            println("✓ 参数:")
            result.arguments.forEach { (key, value) ->
                println("  $key: $value")
            }
        } else {
            println("✗ 错误: ${result.errorMessage}")
        }
        println("-".repeat(5))
    }
}

/**
 * 主函数运行演示
 */
fun main() {
    val demo = ParameterExtractionDemo()

    // 运行各个演示场景
    demo.demoRequiredParameters()
    demo.demoAllParameters()
    demo.demoCustomParameters()
    demo.demoCompleteWorkflow()

    // 额外演示：不同语法模式对比
    println("\n=== 语法模式对比 ===")
    val syntaxes = listOf(
        "cmd <req1> <req2> [opt1] [opt2]",
        "run {command} {args}",
        "test <input> [output] --mode {mode} --verbose {flag}"
    )

    syntaxes.forEach { syntax ->
        println("\n语法: $syntax")
        println("尖括号参数: ${demo.extractParameterNames(syntax, ParameterExtractionDemo.ANGLE_BRACKETS)}")
        println("方括号参数: ${demo.extractParameterNames(syntax, ParameterExtractionDemo.SQUARE_BRACKETS)}")
        println("花括号参数: ${demo.extractParameterNames(syntax, ParameterExtractionDemo.CURLY_BRACES)}")
        println("所有参数: ${demo.extractParameterNames(syntax, ParameterExtractionDemo.DEFAULT_PARAMETER_PATTERN)}")
    }
}