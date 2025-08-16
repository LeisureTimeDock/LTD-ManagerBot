
package top.r3944realms.ltdmanager.napcat.requests.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
