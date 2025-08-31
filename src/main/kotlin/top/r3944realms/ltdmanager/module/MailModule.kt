package top.r3944realms.ltdmanager.module

import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import top.r3944realms.ltdmanager.core.mail.Mail
import top.r3944realms.ltdmanager.utils.LoggerUtil
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class MailModule(
    private val protocol: String = "SMTP",
    private val host: String,
    private val port: Int,
    private val senderEmailAddress: String,
    private val authToken: String,
    private val enableAuth: Boolean = true,
    private val enableTLS: Boolean = true,
    private val intervalMillis: Long = 2000L // 每封邮件之间的间隔（默认 2s）
) : BaseModule() {

    override val name: String = "MailModule"

    private lateinit var session: Session
    private val queue = LinkedBlockingQueue<Mail>()  // 邮件队列
    private var workerThread: Thread? = null
    @Volatile private var running = false

    override fun onLoad() {
        LoggerUtil.logger.info("[$name] 模块加载中，初始化邮件会话...")
        /*
        163 邮箱（以及大多数 SMTP 服务商）区别是：

        465 👉 “隐式 SSL”，必须启用 mail.smtp.ssl.enable=true。

        587 👉 “明文 + STARTTLS”，必须启用 mail.smtp.starttls.enable=true。

        而注释中 onLoad() 写死了：

        put("mail.smtp.starttls.enable", enableTLS)

        所以当用 465 端口时，服务端要求立即握手 SSL，但程序还在用明文 STARTTLS，直接就被 EOF 掉了。
        * */
        //        val props = Properties().apply {
        //            put("mail.transport.protocol", protocol)
        //            put("mail.smtp.host", host)
        //            put("mail.smtp.port", port)
        //            put("mail.smtp.auth", enableAuth)
        //            put("mail.smtp.starttls.enable", enableTLS)
        //        }
        val props = Properties().apply {
            put("mail.transport.protocol", protocol)
            put("mail.smtp.host", host)
            put("mail.smtp.port", port)
            put("mail.smtp.auth", enableAuth)

            when (port) {
                465 -> {
                    // 隐式 SSL
                    put("mail.smtp.ssl.enable", "true")
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                    put("mail.smtp.socketFactory.port", port.toString())
                }
                587 -> {
                    // STARTTLS
                    if (enableTLS) {
                        put("mail.smtp.starttls.enable", "true")
                        put("mail.smtp.starttls.required", "true")
                    }
                }
                else -> {
                    // 普通 25 端口或其他情况
                    if (enableTLS) {
                        put("mail.smtp.starttls.enable", "true")
                    }
                }
            }
        }
        session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmailAddress, authToken)
            }
        })

        running = true
        workerThread = thread(start = true, name = "MailSender-Worker") {
            LoggerUtil.logger.info("[$name] 邮件发送线程启动")
            while (running && loaded) {
                try {
                    val mail = queue.take() // 阻塞等待新任务
                    LoggerUtil.logger.info("[$name] 开始发送邮件 -> 收件人: ${mail.to.joinToString(",")}")
                    sendInternal(mail)
                    LoggerUtil.logger.info("[$name] 邮件发送完成 -> ${mail.to.joinToString(",")}")
                    Thread.sleep(intervalMillis) // 限流
                } catch (e: InterruptedException) {
                    LoggerUtil.logger.info("[$name] 邮件发送线程被中断，准备退出")
                    break
                } catch (e: Exception) {
                    LoggerUtil.logger.error("[$name] 邮件发送出现异常", e)
                }
            }
        }
    }

    override suspend fun onUnload() {
        LoggerUtil.logger.info("[$name] 模块卸载，停止邮件发送线程")
        running = false
        workerThread?.interrupt()
        workerThread = null
    }


    /**
     * 加入发送队列
     */
    fun enqueue(mail: Mail) {
        if (!loaded) throw IllegalStateException("MailModule 未加载，不能发送邮件")
        LoggerUtil.logger.info("[$name] 邮件加入队列 -> 收件人: ${mail.to.joinToString(",")}, 主题: ${mail.subject}")
        queue.put(mail)
    }

    /**
     * 真正的发送逻辑（内部调用）
     */
    private fun sendInternal(mail: Mail) {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(senderEmailAddress,mail.from ?: senderEmailAddress, "UTF-8"))
            setRecipients(Message.RecipientType.TO, mail.to.joinToString(","))
            if (mail.cc.isNotEmpty()) {
                setRecipients(Message.RecipientType.CC, mail.cc.joinToString(","))
            }
            if (mail.bcc.isNotEmpty()) {
                setRecipients(Message.RecipientType.BCC, mail.bcc.joinToString(","))
            }
            subject = mail.subject
            setContent(
                mail.body,
                if (mail.isHtml) "text/html;charset=UTF-8" else "text/plain;charset=UTF-8"
            )
        }

        Transport.send(message)
    }
}
