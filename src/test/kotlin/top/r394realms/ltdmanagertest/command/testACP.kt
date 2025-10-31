package top.r394realms.ltdmanagertest.command

import top.r3944realms.ltdmanager.module.common.AdvancedCommandParser

fun main() {
    val parser = AdvancedCommandParser().apply {
        registerCommand(
            name = "send",
            aliases = listOf("s"),
            syntax = "<message> [target] [priority]",
            description = "发送消息到指定目标"
        )

        registerCommand(
            name = "user",
            aliases = listOf("u"),
            syntax = "<action> <id> [name] [email] --role {role}",
            description = "用户管理命令"
        )

        registerCommand(
            name = "database",
            aliases = listOf("db"),
            syntax = "<operation> <table> [where] [limit] --format {format}",
            description = "数据库操作命令"
        )
    }

    // 测试复杂命令
    val testCommands = listOf(
        "database select users --where \"age > 18 and name = 'John Doe'\" --limit 10 --format json",
        "send \"Hello, World!\" @all --priority high",
        "user add 123 --email john@example.com --role admin --name \"John Smith\"",
        "invalid command test"
    )

    testCommands.forEach { input ->
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

    // 显示帮助信息
    println(parser.getCommandHelp("database") ?: "命令未找到")
}