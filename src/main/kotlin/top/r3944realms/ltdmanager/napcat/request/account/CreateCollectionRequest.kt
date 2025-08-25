
package top.r3944realms.ltdmanager.napcat.request.account

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * CreateCollection请求
 */
@Serializable
data class CreateCollectionRequest(
    /**
     * 标题
     */
    val brief: String,

    /**
     * 内容
     */
    val rawData: String
) : AbstractAccountRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/create_collection"
}
