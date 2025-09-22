package top.r3944realms.ltdmanager.dglab.model.game

import com.r3944realms.dg_lab.api.operation.ClientOperation
import com.r3944realms.dg_lab.api.websocket.message.data.PowerBoxData

class GameClientOperation(
    val player: Player
) : ClientOperation {

    override fun ClientStartingHandler() {
        println("Player ${player.id} is starting the client...")
    }

    override fun ClientStartedHandler() {
        println("Player ${player.id} client started successfully.")
    }

    override fun ClientStartingErrorHandler() {
        println("Player ${player.id} failed to start client!")
    }

    override fun ClientStoppingHandler() {
        println("Player ${player.id} is stopping the client...")
    }

    override fun ClientStoppingErrorHandler() {
        println("Player ${player.id} encountered an error while stopping.")
    }

    override fun ClientStoppedHandler() {
        println("Player ${player.id} client stopped.")
    }

    override fun QrCodeUrlHandler(p0: String?) {
        println("Player ${player.id} QR code received: $p0")
    }

    override fun ShowQrCodeHandler() {
        println("Player ${player.id} should display QR code.")
    }

    override fun ConnectSuccessfulNoticeHandler() {
        println("Player ${player.id} connected successfully.")
    }

    override fun DisconnectHandler(p0: PowerBoxData?) {
        println("Player ${player.id} disconnected: $p0")
    }

    override fun ErrorHandler(p0: PowerBoxData?) {
        println("Player ${player.id} error occurred: $p0")
    }

    override fun HeartBeatHandler(p0: PowerBoxData?) {
        println("Heartbeat from player ${player.id}: $p0")
    }

    override fun OtherMessageHandler(p0: PowerBoxData?) {
        println("Other message for player ${player.id}: $p0")
    }
}