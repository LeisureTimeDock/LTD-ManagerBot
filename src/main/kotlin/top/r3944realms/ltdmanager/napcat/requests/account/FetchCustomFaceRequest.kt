
package top.r3944realms.ltdmanager.napcat.requests.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * FetchCustomFace请求
 */
@Serializable
data class FetchCustomFaceRequest(
    val count: Double? = null
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/fetch_custom_face"
}
