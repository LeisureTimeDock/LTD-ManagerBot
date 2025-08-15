package top.r3944realms.ltdmanager.utils

import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {
    private const val SECRET_KEY = "ltd25r3944realms"
    private const val ALGORITHM = "AES"

    private val secretKey: String
        get() {
            return YamlConfigLoader.loadCryptoConfig().secretKey ?: SECRET_KEY
        }

    // 解密
    fun decrypt(encryptedText: String): String {
        return decrypt(encryptedText, secretKey)
    }

    // 加密
    fun encrypt(plainText: String): String {
        return encrypt(plainText, secretKey)
    }

    fun decrypt(encryptedText: String, secretKey: String): String {
        try {
            val key = SecretKeySpec(secretKey.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key)

            val decodedBytes = Base64.getDecoder().decode(encryptedText)
            val decryptedBytes = cipher.doFinal(decodedBytes)

            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw RuntimeException("解密失败", e)
        }
    }

    fun encrypt(plainText: String, secretKey: String): String {
        try {
            val key = SecretKeySpec(secretKey.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            throw RuntimeException("加密失败", e)
        }
    }
}