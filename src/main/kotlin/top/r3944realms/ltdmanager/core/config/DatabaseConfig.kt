package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater
import java.util.*

data class DatabaseConfig(
    var url: String? = null,
    var user: String? = null,
    var encryptedPassword: String? = null
) {
    /**
     * 获取解密后的密码（如果未加密，返回原值）
     */
    val decryptedPassword: String?
        get() {
            if (encryptedPassword == null) {
                return null
            }
            if (!isEncrypted()) {
                return encryptedPassword
            }
            try {
                val cipherText = encryptedPassword!!.substring(4, encryptedPassword!!.length - 1)
                return CryptoUtil.decrypt(cipherText)
            } catch (e: Exception) {
                throw IllegalStateException("密码解密失败", e)
            }
        }

    /**
     * 加密密码（如果未加密），并返回是否成功加密
     */
    fun encryptPassword() {
        if (encryptedPassword == null || isEncrypted()) {
            return
        }
        try {
            encryptedPassword = "ENC(${CryptoUtil.encrypt(encryptedPassword!!)})"
            YamlUpdater.updateYamlValue(
                Objects.requireNonNull(
                    YamlConfigLoader::class.java
                        .classLoader
                        .getResource("application.yaml")
                ).path,
                "database.encrypted-password",
                this.encryptedPassword!!
            )
        } catch (e: Exception) {
            throw IllegalStateException("密码加密失败", e)
        }
    }

    /**
     * 检查密码是否已加密
     */
    fun isEncrypted(): Boolean {
        return encryptedPassword != null &&
                encryptedPassword!!.startsWith("ENC(") &&
                encryptedPassword!!.endsWith(")")
    }

    override fun toString(): String {
        return "DatabaseConfig(url=$url, user=$user, encryptedPassword=***)"
    }
}