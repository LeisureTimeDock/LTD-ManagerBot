package top.r3944realms.ltdmanager.core.mail

data class Mail(
    val from: String? = null,           // 发件人
    val to: List<String>,               // 收件人（至少一个）
    val subject: String,                // 邮件主题
    val body: String,                   // 邮件正文
    val isHtml: Boolean = false,        // 是否 HTML
    val cc: List<String> = emptyList(), // 抄送
    val bcc: List<String> = emptyList() // 密送
) {
    companion object {
        fun simple(
            to: String,
            subject: String,
            body: String,
            isHtml: Boolean = false
        ): Mail = Mail(
            to = listOf(to),
            subject = subject,
            body = body,
            isHtml = isHtml
        )

        fun multiple(
            to: List<String>,
            subject: String,
            body: String,
            isHtml: Boolean = false
        ): Mail = Mail(
            to = to,
            subject = subject,
            body = body,
            isHtml = isHtml
        )
    }

}