package top.r3944realms.ltdmanager.dglab.manager

import com.r3944realms.dg_lab.api.manager.IDGLabManager
import com.r3944realms.dg_lab.api.manager.Status
import com.r3944realms.dg_lab.api.websocket.sharedData.ISharedData
import com.r3944realms.dg_lab.manager.DGPBServerManager

class ServerManager(
    private val server: DGPBServerManager
) : IManager<DGPBServerManager>, IDGLabManager {

    override fun startAll() {
        start()
    }

    override fun stopAll() {
        stop()
    }

    override fun start() {
        server.start()
    }

    override fun stop() {
        server.stop()
    }

    override fun getSharedData(): ISharedData {
        return server.sharedData
    }

    override fun getStatus(): Status {
        return server.status
    }

    override fun setStatus(p0: Status?) {
        server.status = p0
    }

    override fun getInstance(): DGPBServerManager {
        return server
    }
}
