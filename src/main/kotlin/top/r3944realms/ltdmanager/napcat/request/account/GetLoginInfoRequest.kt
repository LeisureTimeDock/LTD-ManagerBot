
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable

/**
 * GetLoginInfo请求
 */
@Serializable
class GetLoginInfoRequest : AbstractAccountRequest() {
    override fun toJSON(): String = "{}"

    override fun path(): String = "/get_login_info"
}
