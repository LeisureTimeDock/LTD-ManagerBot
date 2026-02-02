package top.r3944realms.ltdmanager.chevereto.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.chevereto.response.CheveretoResponse
import top.r3944realms.ltdmanager.chevereto.response.FailedCheveretoResponse
import top.r3944realms.ltdmanager.core.client.request.IRequest

@Serializable
abstract class CheveretoRequest(
    @Transient
    override val createTime: Long = System.currentTimeMillis()
) : IRequest<CheveretoResponse, FailedCheveretoResponse>