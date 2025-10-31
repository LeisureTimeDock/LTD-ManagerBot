package top.r394realms.ltdmanagertest.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument

fun main() {
    val dispatcher = CommandDispatcher<String>() // String 表示消息来源

    dispatcher.register(
        literal<String>("say")
            .then(argument<String, String>("message", StringArgumentType.greedyString())
                .executes { ctx ->
                    val msg = StringArgumentType.getString(ctx, "message")
                    println("[BOT] $msg")
                    1
                })
    )
    dispatcher.register(
        literal<String>("add")
            .then(argument<String, Int>("a", IntegerArgumentType.integer())
                .then(argument<String, Int>("b", IntegerArgumentType.integer())
                    .executes { ctx ->
                        val a = IntegerArgumentType.getInteger(ctx, "a")
                        val b = IntegerArgumentType.getInteger(ctx, "b")
                        println("[BOT] $a + $b = ${a + b}")
                        1
                    }))
    )
    dispatcher.execute("say Hello World", "user123")
    dispatcher.execute("add 3 7", "user123")
}