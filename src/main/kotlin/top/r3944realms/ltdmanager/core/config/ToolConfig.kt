package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater

data class ToolConfig(
    var rcon: RconConfig = RconConfig()
) {
    data class RconConfig(
        var mcRconToolPath: String? = null,
        var mcRconToolConfigPath: String? = null,
        var serverUrl: String? = null,
        var rconPassword: String? = null
    ) {
        /**
         * 获取解密后的 rcon 密码（如果未加密，返回原值）
         */
        val decryptedPassword: String?
            get() {
                if (rconPassword == null) return null
                if (!isEncrypted()) return rconPassword
                return try {
                    val cipherText = rconPassword!!.substring(4, rconPassword!!.length - 1)
                    CryptoUtil.decrypt(cipherText)
                } catch (e: Exception) {
                    throw IllegalStateException("RCON 密码解密失败", e)
                }
            }

        /**
         * 加密 rcon 密码（如果未加密）
         */
        fun encryptPassword(configFilePath: String) {
            if (rconPassword == null || isEncrypted()) return
            try {
                rconPassword = "ENC(${CryptoUtil.encrypt(rconPassword!!)})"
                YamlUpdater.updateYaml(
                    configFilePath,
                    "tools.rcon.rcon-password",
                    rconPassword!!
                )
            } catch (e: Exception) {
                throw IllegalStateException("RCON 密码加密失败", e)
            }
        }

        private fun isEncrypted(): Boolean {
            return rconPassword != null &&
                    rconPassword!!.startsWith("ENC(") &&
                    rconPassword!!.endsWith(")")
        }

        override fun toString(): String {
            return "RconConfig(path=$mcRconToolPath, configPath=$mcRconToolConfigPath, url=$serverUrl, password=***)"
        }
    }

    override fun toString(): String {
        return "ToolConfig(rcon=$rcon)"
    }
}