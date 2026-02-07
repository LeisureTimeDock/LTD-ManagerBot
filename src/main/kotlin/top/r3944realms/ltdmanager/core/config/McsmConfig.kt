package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater

data class McsmConfig(
    var url: String ?= null,
    var encryptedApiKey: String ?= null,
    var instanceID: String ?= null,
    ) {
    val decryptedApiKey: String?
        get() {
            if (encryptedApiKey == null) return null
            if (!isEncrypted()) return encryptedApiKey
            try {
                val cipherText = encryptedApiKey!!.substring(4, encryptedApiKey!!.length - 1)
                return CryptoUtil.decrypt(cipherText)
            } catch (e: Exception) {
                throw IllegalStateException("API解密失败", e)
            }
        }

    /**
     * 加密密码（如果未加密），并写回配置文件
     */
    fun encryptApi() {
        if (encryptedApiKey == null || isEncrypted()) {
            return
        }
        try {
            encryptedApiKey = "ENC(${CryptoUtil.encrypt(encryptedApiKey!!)})"
            YamlUpdater.updateYaml(
                YamlConfigLoader.appConfigFilePath.toString(),
                "mcsm.encrypted-api-key",
                this.encryptedApiKey!!
            )
        } catch (e: Exception) {
            throw IllegalStateException("API加密失败", e)
        }
    }

    /**
     * 检查密码是否已加密
     */
    private fun isEncrypted(): Boolean {
        return encryptedApiKey != null &&
                encryptedApiKey!!.startsWith("ENC(") &&
                encryptedApiKey!!.endsWith(")")
    }

    override fun toString(): String {
        return "McsmConfig(url=$url, api-key=***)"
    }
}