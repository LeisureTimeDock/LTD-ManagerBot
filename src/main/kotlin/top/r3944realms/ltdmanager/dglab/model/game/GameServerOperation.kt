package top.r3944realms.ltdmanager.dglab.model.game

import com.r3944realms.dg_lab.api.websocket.message.PowerBoxMessage
import com.r3944realms.dg_lab.api.websocket.message.role.PlaceholderRole
import com.r3944realms.dg_lab.api.websocket.message.role.WebSocketServerRole
import com.r3944realms.dg_lab.websocket.handler.server.DefaultServerOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.r3944realms.ltdmanager.dglab.DgLab
import top.r3944realms.ltdmanager.dglab.manager.ServerManager
import top.r3944realms.ltdmanager.napcat.NapCatClient
import top.r3944realms.ltdmanager.napcat.data.ID
import top.r3944realms.ltdmanager.napcat.data.MessageElement
import top.r3944realms.ltdmanager.napcat.request.other.SendGroupMsgRequest

class GameServerOperation(private val msgClient: NapCatClient, val groupId: Long) : DefaultServerOperation() {
    private val scope = CoroutineScope(Dispatchers.IO)
    var serverManager: ServerManager? = null
    override fun ServerStartingHandler() {
        scope.launch {
            msgClient.sendUnit(
                SendGroupMsgRequest(listOf(MessageElement.text("服务器启动中...")), ID.long(groupId))
            )
        }
    }

    override fun ServerStartedHandler() {
        scope.launch {
            msgClient.sendUnit(
                SendGroupMsgRequest(listOf(MessageElement.text("服务器已启动")), ID.long(groupId))
            )
        }
    }
    override fun ServerStoppingHandler() {
        scope.launch {
            msgClient.sendUnit(
                SendGroupMsgRequest(listOf(MessageElement.text("服务器关闭中...")), ID.long(groupId))
            )
        }
    }

    override fun ServerStoppedHandler() {
        scope.launch {
            msgClient.sendUnit(
                SendGroupMsgRequest(listOf(MessageElement.text("服务器已关闭")), ID.long(groupId))
            )
        }
    }

    override fun ServerStoppingErrorHandler(errMsg: String) {
        scope.launch {
            msgClient.sendUnit(
                SendGroupMsgRequest(listOf(MessageElement.text("服务器关闭过程中遇到错误: $errMsg")), ID.long(groupId))
            )
        }
    }

    override fun ServerStartingErrorHandler(errMsg: String?) {
        scope.launch {
            msgClient.sendUnit(
                SendGroupMsgRequest(listOf(MessageElement.text("服务器开启过程中遇到错误: $errMsg")), ID.long(groupId))
            )
        }
    }

    override fun ClientSessionBuildInHandler(clientId: String?) {
        scope.launch{
            delay(1000)
            serverManager?.getInstance()?.send(
                clientId,
                PowerBoxMessage.createPowerBoxMessage(
                    "bind",
                    clientId,
                    "",
                    "",
                    WebSocketServerRole(DgLab.SERVER_ROLE_NAME),
                    PlaceholderRole("Temp-$clientId")
                )
            )
        }
    }
}