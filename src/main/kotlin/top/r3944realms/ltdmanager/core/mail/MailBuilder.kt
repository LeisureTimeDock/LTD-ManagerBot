package top.r3944realms.ltdmanager.core.mail

class MailBuilder {
    var from: String? = null
    val to = mutableListOf<String>()
    var subject: String = ""
    var body: String = ""
    var isHtml: Boolean = false
    val cc = mutableListOf<String>()
    val bcc = mutableListOf<String>()

    fun build() = Mail(from, to, subject, body, isHtml, cc, bcc)
}

fun mail(block: MailBuilder.() -> Unit): Mail {
    val builder = MailBuilder()
    builder.block()
    return builder.build()
}

