package top.r3944realms.ltdmanager.utils

import java.io.InputStreamReader
import java.nio.charset.Charset
import java.sql.Timestamp
import java.text.SimpleDateFormat


object HtmlTemplateUtil {

    /**
     * 从指定 HTML 文件读取内容并替换占位符
     * @param resourcePath HTML 文件路径
     * @param variables 占位符变量，如 mapOf("名字" to "小明", "时间" to "2025-08-28")
     * @param charset 文件编码，默认 UTF-8
     */
    fun renderTemplateFromClasspath(
        resourcePath: String,
        variables: Map<String, String>,
        charset: Charset = Charsets.UTF_8
    ): String {
        val inputStream = object {}.javaClass.classLoader.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("模板文件未找到: $resourcePath")

        val template = InputStreamReader(inputStream, charset).use { it.readText() }

        var result = template
        variables.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }

        return result
    }

    /**
     * 生成激活码邮件 HTML
     */
    fun tokenMailHtmlTemplate(
        playerName: String,
        token: String,
        expireTime: Timestamp? = null,
        validDay: Int? = null,
        timeYear: Int
    ): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return renderTemplateFromClasspath(
            resourcePath = "mail-body.html",
            variables = mapOf(
                "player_name" to playerName,
                "activation_code" to token,
                "expire_time" to (expireTime?.let { sdf.format(it) } ?: "永久有效"),
                "valid_days" to (validDay?.toString() ?: "INF"),
                "time_year" to timeYear.toString()
            )
        )
    }
}
