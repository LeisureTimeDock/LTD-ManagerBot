
package top.r3944realms.ltdmanager.napcat.request.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * GetCollectionList请求
 */
@Developing
@Serializable
data class GetCollectionListRequest(
    val category: Int,
    val count: Int
) : AbstractOtherRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/get_collection_list"
}
