package top.r394realms.ltdmanagertest.command

import top.r3944realms.ltdmanager.module.common.AdvancedCommandParser

fun main() {
    val parser = AdvancedCommandParser().apply {
        registerCommand(
            name = "send",
            aliases = listOf("s"),
            syntax = "<message> [target]",
            description = "发送消息到指定目标"
        )

        registerCommand(
            name = "user",
            aliases = listOf("u"),
            syntax = "<action> <id> [options]",
            description = "用户管理命令"
        )

        registerCommand(
            name = "config",
            aliases = listOf("cfg"),
            syntax = "set <key> <value> | get <key>",
            description = "配置管理",
            parameterPattern = AdvancedCommandParser.ANGLE_BRACKETS
        )
    }

    // 测试解析
    val testInputs = listOf(
        "send Hello World",
        "user add 123 --name John",
        "config set theme dark",
        "invalid command"
    )

    testInputs.forEach { input ->
        println("输入: $input")
        val result = parser.parseAndValidate(input)
        println("结果: $result")
        println("---")
    }

    // 获取帮助信息
    println("帮助信息:")
    println(parser.getCommandHelp("send"))
}