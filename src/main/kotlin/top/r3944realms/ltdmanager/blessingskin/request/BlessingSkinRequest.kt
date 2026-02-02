package top.r3944realms.ltdmanager.blessingskin.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.blessingskin.response.BlessingSkinResponse
import top.r3944realms.ltdmanager.blessingskin.response.FailedBlessingSkinResponse
import top.r3944realms.ltdmanager.core.client.request.IRequest

@Serializable
abstract class BlessingSkinRequest(
    @Transient
    override val createTime: Long = System.currentTimeMillis()
): IRequest<BlessingSkinResponse, FailedBlessingSkinResponse>