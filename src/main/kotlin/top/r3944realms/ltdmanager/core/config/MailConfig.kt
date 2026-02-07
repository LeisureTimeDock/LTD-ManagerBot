package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater

data class MailConfig(
    var host: String? = null,                 // SMTP 主机
    var port: Int? = 587,                     // 端口（25/465/587）
    var mailAddress: String? = null,         // 邮箱账号
    var encryptedPassword: String? = null,    // 加密后的密码或明文
    var auth: Boolean? = true,                // 是否需要认证
    var tls: Boolean? = true,                 // 是否启用 STARTTLS
    var protocol: String = "smtp"             // 协议，默认 smtp
) {
    val decryptedPassword: String?
        get() {
            if (encryptedPassword == null) return null
            if (!isEncrypted()) return encryptedPassword
            try {
                val cipherText = encryptedPassword!!.substring(4, encryptedPassword!!.length - 1)
                return CryptoUtil.decrypt(cipherText)
            } catch (e: Exception) {
                throw IllegalStateException("邮件密码解密失败", e)
            }
        }

    /**
     * 加密密码（如果未加密），并写回配置文件
     */
    fun encryptPassword() {
        if (encryptedPassword == null || isEncrypted()) {
            return
        }
        try {
            encryptedPassword = "ENC(${CryptoUtil.encrypt(encryptedPassword!!)})"
            YamlUpdater.updateYaml(
                YamlConfigLoader.appConfigFilePath.toString(),
                "mail.encrypted-password",
                this.encryptedPassword!!
            )
        } catch (e: Exception) {
            throw IllegalStateException("邮件密码加密失败", e)
        }
    }

    /**
     * 检查密码是否已加密
     */
    private fun isEncrypted(): Boolean {
        return encryptedPassword != null &&
                encryptedPassword!!.startsWith("ENC(") &&
                encryptedPassword!!.endsWith(")")
    }

    override fun toString(): String {
        return "MailConfig(host=$host, port=$port, emailAddress=$mailAddress, password=***)"
    }
}