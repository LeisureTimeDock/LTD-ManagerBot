package top.r3944realms.ltdmanager.utils

import java.io.BufferedReader
import java.io.InputStreamReader

open class CmdUtil {
     companion object {
        fun runExeCommand(exePath: String, vararg args: String): String {
            // 构建命令
            val command = mutableListOf(exePath)
            command.addAll(args)

            // 启动进程
            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(true) // 将错误流也合并到标准输出

            val process = processBuilder.start()

            // 读取输出
            val output = StringBuilder()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.appendLine(line)
                }
            }

            // 等待程序执行完成
            process.waitFor()

            return output.toString()
        }
    }
}