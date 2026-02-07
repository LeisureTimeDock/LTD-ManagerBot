package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater

data class HttpConfig(
    var url: String? = null,
    var encryptedToken: String? = null
) {
    /**
     * 获取解密后的token（如果未加密，返回原值）
     */
    val decryptedToken: String?
        get() {
            if (encryptedToken == null) {
                return null
            }
            if (!isEncrypted()) {
                return encryptedToken
            }
            try {
                val cipherText = encryptedToken!!.substring(4, encryptedToken!!.length - 1)
                return CryptoUtil.decrypt(cipherText)
            } catch (e: Exception) {
                throw IllegalStateException("token解密失败", e)
            }
        }

    /**
     * 加密密码（如果未加密），并返回是否成功加密
     */
    fun encryptToken() {
        if (encryptedToken == null || isEncrypted()) {
            return
        }
        try {
            encryptedToken = "ENC(${CryptoUtil.encrypt(encryptedToken!!)})"
            YamlUpdater.updateYaml(
                YamlConfigLoader.appConfigFilePath.toString(),
                "http.encrypted-token",
                this.encryptedToken!!
            )
        } catch (e: Exception) {
            throw IllegalStateException("密码加密失败", e)
        }
    }

    /**
     * 检查Token是否已加密
     */
    private fun isEncrypted(): Boolean {
        return encryptedToken != null &&
                encryptedToken!!.startsWith("ENC(") &&
                encryptedToken!!.endsWith(")")
    }
    override fun toString(): String {
        return "HttpConfig(Url=$url, token=***)"
    }
}