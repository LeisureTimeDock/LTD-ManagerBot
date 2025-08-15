package top.r3944realms.ltdmanager.core.config

data class CryptoConfig(
    var secretKey: String? = null
) {
    override fun toString(): String {
        return "CryptoConfig(secretkeu=$secretKey)"
    }
}
