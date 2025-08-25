
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.Serializable
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * Unknown请求
 */
@Developing
@Serializable
class UnknownRequest: AbstractOtherRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/unknown"
}
