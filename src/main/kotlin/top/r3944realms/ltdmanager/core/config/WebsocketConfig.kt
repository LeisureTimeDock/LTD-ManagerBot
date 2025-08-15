package top.r3944realms.ltdmanager.core.config

data class WebsocketConfig(
    var url: String? = null,
    var token: String? = null
) {
    override fun toString(): String {
        return "WebsocketConfig(Url=$url, token=$token)"
    }
}
