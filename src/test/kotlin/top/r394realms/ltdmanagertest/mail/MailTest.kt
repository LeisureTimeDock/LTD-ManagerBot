package top.r394realms.ltdmanagertest.mail

import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.core.mail.mail
import top.r3944realms.ltdmanager.module.MailModule
import top.r3944realms.ltdmanager.utils.HtmlTemplateUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() = GlobalManager.runBlockingMain {
    val mailConfig = YamlConfigLoader.loadMailConfig()
    val mailModule = mailConfig.port?.let { portIt ->
        mailConfig.mailAddress?.let { mailAddressIt ->
            MailModule(
                moduleName = "WhiteListGroup",
                host = mailConfig.host.toString(),
                authToken = mailConfig.decryptedPassword.toString(),
                port = portIt,
                senderEmailAddress = mailAddressIt,
            )
        }
    }
    if (mailModule == null) throw IllegalStateException("Lost Required Argument")
    GlobalManager.moduleManager.register(mailModule)

    GlobalManager.moduleManager.load(mailModule.name)
    val template = object {}.javaClass.classLoader
        .getResource("mail-body.html")?: throw IllegalArgumentException("模板文件未找到")
    val expireHours = 24 // 有效期 24 小时
    val expireTime = LocalDateTime.now().plusHours(expireHours.toLong())
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val bodyC = HtmlTemplateUtil.renderTemplateFromClasspath(template.file.toString(), mapOf(
        "player_name" to "小明",
        "activation_code" to "ABC123",
        "expire_time" to expireTime,
        "valid_days" to "${expireHours/24}",
        "time_year" to "2025"
    ))
    val mail = mail {
        from = "闲趣时坞"
        to += "f256198830@hotmail.com"
        subject = "=-="
        body = bodyC
        isHtml = true
        cc += "f256198830@outlook.com"
    }
    mailModule.enqueue(mail)
}