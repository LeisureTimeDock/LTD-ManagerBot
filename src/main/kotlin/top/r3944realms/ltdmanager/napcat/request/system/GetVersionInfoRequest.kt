
package top.r3944realms.ltdmanager.napcat.request.system

import kotlinx.serialization.Serializable

/**
 * GetVersionInfo请求
 */
@Serializable
class GetVersionInfoRequest : AbstractSystemRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_version_info"
}
