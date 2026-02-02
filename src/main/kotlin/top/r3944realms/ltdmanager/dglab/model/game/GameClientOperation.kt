package top.r3944realms.ltdmanager.dglab.model.game

import com.r3944realms.dg_lab.api.operation.ClientOperation
import com.r3944realms.dg_lab.api.websocket.message.data.PowerBoxData
import com.r3944realms.dg_lab.api.websocket.message.data.type.PowerBoxDataType
import com.r3944realms.dg_lab.manager.DGPBClientManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.r3944realms.ltdmanager.GlobalManager
import top.r3944realms.ltdmanager.chevereto.response.FailedCheveretoResponse
import top.r3944realms.ltdmanager.chevereto.response.v1.CheveretoUploadResponse
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest
import top.r3944realms.ltdmanager.napcat.request.other.SendPrivateMsgRequest
import top.r3944realms.ltdmanager.utils.LoggerUtil
import top.r3944realms.ltdmanager.utils.QRCodeUtil
import java.io.ByteArrayInputStream


class GameClientOperation(
    val napCatClient: NapCatClient,
    val groupId: Long,
    val playerManager: PlayerManager,
    private val playerId: Long
) : ClientOperation {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var qrcode:ByteArrayInputStream? = null;
    var clientSelf: DGPBClientManager? = null
    private var hasBinding = false
    private var bindingTimeoutJob: kotlinx.coroutines.Job? = null // 保存倒计时任务
    override fun ClientStartingHandler() {
        LoggerUtil.logger.debug("Player $playerId is starting the client...")
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("DG_LAB客户端启动中...")), ID.long(playerId)))
        }
    }

    override fun ClientStartedHandler() {
        LoggerUtil.logger.debug("Player $playerId client started successfully.")
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("DG_LAB客户端启动完成!")), ID.long(playerId)))
        }
        playerManager.getPlayer(playerId)?.active = true
    }

    override fun ClientStartingErrorHandler(errMsg: String) {
        LoggerUtil.logger.debug("Player $playerId failed to start client! Reason: $errMsg")
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("DG_LAB客户端启动中遇到错误：$errMsg!")), ID.long(playerId)))
        }
        playerManager.getPlayer(playerId)?.active = false
    }

    override fun ClientStoppingHandler() {
        LoggerUtil.logger.debug("Player $playerId is stopping the client...")
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("DG_LAB客户端关闭中...")), ID.long(playerId)))
        }
        playerManager.getPlayer(playerId)?.active = false
    }

    override fun ClientStoppingErrorHandler(errMsg: String) {
        LoggerUtil.logger.debug("Player $playerId encountered an error while stopping. Reason: $errMsg")
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("DG_LAB客户端关闭中遇到错误：$errMsg!")), ID.long(playerId)))
        }
        playerManager.getPlayer(playerId)?.active = false
    }

    override fun ClientStoppedHandler() {
        LoggerUtil.logger.debug("Player $playerId client stopped.")
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("DG_LAB客户端成功关闭!")), ID.long(playerId)))
        }
        bindingTimeoutJob?.cancel()
        playerManager.getPlayer(playerId)?.active = false
    }

    override fun QrCodeUrlHandler(p0: String?) {
        LoggerUtil.logger.debug("Player $playerId QR code received: $p0")

        if (p0.isNullOrBlank()) {
            LoggerUtil.logger.warn("二维码 URL 为空，无法生成")
            return
        }
        // 处理 URL，将 IP 和端口替换为配置文件中的服务器 URL
        val processedUrl = processQrCodeUrl(p0)

        // 生成二维码文件
        qrcode = QRCodeUtil.generateQRCode(processedUrl, 300, 300)

    }
    /**
     * 处理二维码 URL，将整个连接地址替换为配置文件中的服务器 URL
     */
    private fun processQrCodeUrl(originalUrl: String): String {
        return try {
            val configUrl = YamlConfigLoader.loadDgLabConfig().wsServer.localServerPublishUrl

            // 使用正则表达式匹配整个 ws:// 或 wss:// 开头的 URL
            val pattern = Regex("""wss?://[^:/]+(?::\d+)?(/.*)?""")

            pattern.replace(originalUrl) { matchResult ->
                // 保留原始 URL 中的路径部分（如果有的话）
                val path = matchResult.groupValues[1]
                "$configUrl$path"
            }
        } catch (e: Exception) {
            LoggerUtil.logger.error("处理二维码 URL 时出错: ${e.message}", e)
            originalUrl // 如果处理失败，返回原 URL
        }
    }
    override fun ShowQrCodeHandler() {
        LoggerUtil.logger.debug("Display QRCode to $playerId.")

        if (qrcode == null) {
            LoggerUtil.logger.warn("没有可用的二维码路径")
            return
        }

        scope.launch {
            // 上传二维码图片
            val response = GlobalManager.cheveretoClient.uploadStream(
                qrcode!!,
                "$playerId-Qrcode-${System.currentTimeMillis()}.png",
                "Qrcode-$playerId-${System.currentTimeMillis()}",
                "5min后将会自动删除",
                albumId = "BFx",
                expiration = "PT5M"
            )
            if (response is CheveretoUploadResponse){
                napCatClient.sendUnit(
                    SendPrivateMsgRequest(
                        listOf(
                            MessageElement.text("请在60s内绑定APP，否则将自动断开连接"),
                            MessageElement.image(response.image.url, "二维码")
                        ),
                        ID.long(playerId)
                    )
                )
            } else if (response is FailedCheveretoResponse.Default){
                napCatClient.sendUnit(
                    SendPrivateMsgRequest(
                        listOf(
                            MessageElement.text("无法上传图片，请联系管理员:${response.httpStatusCode} , ${response.failedMessage}"),
                        ),
                        ID.long(playerId)
                    )
                )
            }
            // 启动 60 秒倒计时任务
            bindingTimeoutJob = launch {
                kotlinx.coroutines.delay(60_000)
                val player = playerManager.getPlayer(playerId)
                if (player != null && !hasBinding) {
                    LoggerUtil.logger.warn("Player $playerId 在 60 秒内未绑定，正在停止客户端")
                    napCatClient.sendUnit(
                        SendPrivateMsgRequest(
                            listOf(
                                MessageElement.text("请在60s内未绑定APP，准备停止客户端"),
                            ),
                            ID.long(playerId)
                        )
                    )
                    try {
                        clientSelf?.stop()
                    } catch (e: Exception) {
                        LoggerUtil.logger.error("停止客户端失败: ", e)
                    } finally {
                        player.active = false
                    }
                }
            }
        }
    }
    override fun ConnectSuccessfulNoticeHandler() {
        LoggerUtil.logger.debug("Player $playerId connected successfully.")
        bindingTimeoutJob?.cancel()
        bindingTimeoutJob = null
        val player = playerManager.getPlayer(playerId)
        player?.active = true
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("恭喜，绑定成功")), ID.long(playerId)))
            napCatClient.sendUnit(SendGroupMsgRequest(listOf(MessageElement.text("$playerId 加入战局")), ID.long(groupId)))
        }
    }

    override fun DisconnectHandler(p0: PowerBoxData?) {
        LoggerUtil.logger.debug("Player {} disconnected: {}", playerId, p0)
        scope.launch {
            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("连接断开, $p0")), ID.long(playerId)))
            napCatClient.sendUnit(SendGroupMsgRequest(listOf(MessageElement.text("$playerId 离开战局")), ID.long(groupId)))
        }
    }

    override fun ErrorHandler(p0: PowerBoxData?) {
        LoggerUtil.logger.debug("Player {} error occurred: {}", playerId, p0)
        scope.launch {
            if(p0 != null && p0.message.isNotEmpty())
                napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("遇到错误, $p0")), ID.long(playerId)))
        }
    }

    override fun HeartBeatHandler(p0: PowerBoxData?) {
//        LoggerUtil.logger.debug("Heartbeat from player {}: {}", playerId, p0)
//        scope.launch {
//            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("连接断开, $p0")), ID.long(playerId)))
//        }
    }

    override fun OtherMessageHandler(p0: PowerBoxData?) {
//        LoggerUtil.logger.debug("Other message for player {}: {}", playerId, p0)
//        scope.launch {
//            napCatClient.sendUnit(SendPrivateMsgRequest(listOf(MessageElement.text("连接断开, $p0")), ID.long(playerId)))
//        }
        when (p0?.commandType) {
            PowerBoxDataType.STRENGTH -> TODO()
            PowerBoxDataType.PULSE -> TODO()
            PowerBoxDataType.CLEAR -> TODO()
            PowerBoxDataType.FEEDBACK -> TODO()
            else -> return
        }
    }
}