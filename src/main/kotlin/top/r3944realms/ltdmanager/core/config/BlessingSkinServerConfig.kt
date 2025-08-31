package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater

data class BlessingSkinServerConfig(
    var url: String ?= null,
    var invitationApi: BlessingSkinServerConfig.InvitationApiConfig?= null
) {
    data class BlessingSkinServerConfig(
        var url: String? = null,
        var invitationApi: InvitationApiConfig? = null
    ) {
        data class InvitationApiConfig(
            var path: String? = null,
            var encryptedToken: String? = null
        ) {
            /**
             * 获取解密后的 token（如果未加密，返回原值）
             */
            val decryptedToken: String?
                get() {
                    if (encryptedToken == null) return null
                    if (!isEncrypted()) return encryptedToken
                    return try {
                        val cipherText = encryptedToken!!.substring(4, encryptedToken!!.length - 1)
                        CryptoUtil.decrypt(cipherText)
                    } catch (e: Exception) {
                        throw IllegalStateException("API token 解密失败", e)
                    }
                }

            /**
             * 加密 token（如果未加密）并写回 YAML
             */
            fun encryptToken() {
                if (encryptedToken == null || isEncrypted()) return
                try {
                    encryptedToken = "ENC(${CryptoUtil.encrypt(encryptedToken!!)})"
                    YamlUpdater.updateYaml(
                        YamlConfigLoader.configFilePath.toString(),
                        "blessing-skin-server.invitation-api.encrypted-token",
                        encryptedToken!!
                    )
                } catch (e: Exception) {
                    throw IllegalStateException("API token 加密失败", e)
                }
            }

            /**
             * 检查是否已加密
             */
            private fun isEncrypted(): Boolean {
                return encryptedToken != null &&
                        encryptedToken!!.startsWith("ENC(") &&
                        encryptedToken!!.endsWith(")")
            }

            /**
             * 获取完整 API URL
             */
            fun getFullUrl(baseUrl: String?): String? {
                if (baseUrl == null || path.isNullOrBlank()) return null
                return baseUrl.trimEnd('/') + "/" + path!!.trimStart('/')
            }

            override fun toString(): String {
                return "InvitationApiConfig(path=$path, token=***)"
            }
        }

        override fun toString(): String {
            return "BlessingSkinServerConfig(url=$url, invitationApi=$invitationApi)"
        }
        }
}