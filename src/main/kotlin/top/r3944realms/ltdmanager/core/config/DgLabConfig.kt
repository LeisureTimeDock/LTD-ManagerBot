package top.r3944realms.ltdmanager.core.config

import top.r3944realms.ltdmanager.utils.CryptoUtil
import top.r3944realms.ltdmanager.utils.YamlUpdater


data class DgLabConfig(
    var wsServer: WsServerConfig = WsServerConfig(),
    var dgLabClient: DgLabClientConfig = DgLabClientConfig(),
    var pulseData: PulseDataConfig = PulseDataConfig(),
    var commandText: CommandTextConfig = CommandTextConfig(),
    var replyText: ReplyTextConfig = ReplyTextConfig(),
    var debug: DebugConfig = DebugConfig()
) {
    data class WsServerConfig(
        var localServerUrl: String = "0.0.0.0",
        var localServerPort: Int = 4567,
        var localServerPublishUrl: String = "ws://127.0.0.1:4567",
        var localServerSecure: Boolean = false,
        var localServerSslCert: String = "",
        var localServerSslKey: String = "",
        var encryptedLocalServerSslPassword: String? = null
    ) {
        val decryptedLocalServerSslPassword: String?
            get() {
                if (encryptedLocalServerSslPassword == null) return null
                if (!isEncrypted()) return encryptedLocalServerSslPassword
                return try {
                    val cipherText = encryptedLocalServerSslPassword!!.substring(4, encryptedLocalServerSslPassword!!.length - 1)
                    CryptoUtil.decrypt(cipherText)
                } catch (e: Exception) {
                    throw IllegalStateException("localServerSslPassword 解密失败", e)
                }
            }

        fun encryptPassword() {
            if (encryptedLocalServerSslPassword == null || isEncrypted()) return
            try {
                encryptedLocalServerSslPassword = "ENC(${CryptoUtil.encrypt(encryptedLocalServerSslPassword!!)})"
                YamlUpdater.updateYaml(
                    YamlConfigLoader.appConfigFilePath.toString(),
                    "dg-lab.ws-server.encrypted-local-server-ssl-password",
                    encryptedLocalServerSslPassword!!
                )
            } catch (e: Exception) {
                throw IllegalStateException("SSL 密码加密失败", e)
            }
        }

        private fun isEncrypted(): Boolean {
            return encryptedLocalServerSslPassword != null &&
                    encryptedLocalServerSslPassword!!.startsWith("ENC(") &&
                    encryptedLocalServerSslPassword!!.endsWith(")")
        }
        //TODO: 添加有效性检测
        fun validate() {
            require(localServerUrl.isNotBlank()) { "localServerUrl 未配置" }
            require(localServerPort > 0) { "localServerPort 必须大于 0" }
            require(localServerPublishUrl.isNotBlank()) { "localServerPublishUrl 未配置" }
            if (localServerSecure) {
                require(localServerSslCert.isNotBlank()) { "启用 SSL 时必须配置 localServerSslCert" }
                require(localServerSslKey.isNotBlank()) { "启用 SSL 时必须配置 localServerSslKey" }
            }
        }
    }

    data class DgLabClientConfig(
        var bindTimeout: Double = 90.0,
        var registerTimeout: Double = 30.0
    )

    data class PulseDataConfig(
        var customPulseData: String = "data/dg-lab-play/customPulseData.json",
        var durationPerPost: Double = 8.0,
        var postInterval: Double = 1.0,
        var sleepAfterClear: Double = 0.5
    ) {
        fun validate(maxLength: Double) {
            require(durationPerPost <= maxLength * 0.1) { "PulseDataConfig.durationPerPost 超出最大时长" }
        }
    }

    data class CommandTextConfig(
        var appendPulse: String = "增加波形",
        var currentPulse: String = "当前波形",
        var currentStrength: String = "当前强度",
        var decreaseStrength: String = "减小强度",
        var dgLabDeviceJoin: String = "绑定郊狼",
        var exitGame: String = "退出游戏",
        var increaseStrength: String = "加大强度",
        var randomPulse: String = "随机波形",
        var randomStrength: String = "随机强度",
        var resetPulse: String = "重置波形",
        var showPlayers: String = "当前玩家",
        var showPulses: String = "可用波形",
        var usage: String = "郊狼玩法"
    )

    data class ReplyTextConfig(
        var bindTimeout: String = "绑定超时",
        var currentPlayers: String = "当前玩家：",
        var currentPulse: String = "当前波形循环为：【{}】",
        var currentStrength: String = "A通道：{0}/{1} B通道：{2}/{3}",
        var failedToCreateClient: String = "创建 DG-Lab 控制终端失败",
        var failedToFetchStrengthInfo: String = "获取通道强度状态失败",
        var failedToFetchStrengthLimit: String = "获取通道强度上限失败，控制失败",
        var gameExited: String = "已退出游戏",
        var invalidPulseParam: String = "波形参数错误，控制失败",
        var invalidStrengthParam: String = "强度参数错误，控制失败",
        var invalidTarget: String = "目标玩家不存在或郊狼 App 未绑定",
        var noAvailablePulse: String = "无可用波形",
        var noPlayer: String = "当前没有已连接的玩家，你可以绑定试试~",
        var notBindYet: String = "你目前没有绑定 DG-Lab App",
        var pleaseAtTarget: String = "使用命令的同时请 @ 想要控制的玩家",
        var pleaseScanQrcode: String = "请用 DG-Lab App 扫描二维码以连接",
        var pleaseSetPulseFirst: String = "请先设置郊狼波形：{}",
        var pulsesEmpty: String = "当前波形循环为空",
        var successfullyBind: String = "绑定成功，可以开始色色了！",
        var successfullyDecreased: String = "郊狼强度减小了 {}%",
        var successfullyIncreased: String = "郊狼强度加强了 {}%！",
        var successfullySetPulse: String = "郊狼波形成功设置为【{}】！",
        var successfullySetToStrength: String = "郊狼强度成功设置为 {}%！"
    )

    data class DebugConfig(
        var enableDebug: Boolean = false,
        var ideHost: String = "127.0.0.1",
        var idePort: Int = 5678
    )
}