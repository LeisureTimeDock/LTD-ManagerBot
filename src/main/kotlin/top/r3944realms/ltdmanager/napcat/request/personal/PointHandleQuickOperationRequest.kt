
package top.r3944realms.ltdmanager.napcat.request.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * PointHandleQuickOperation请求
 */
@Serializable
data class PointHandleQuickOperationRequest(
    /**
     * 事件数据对象
     */
    val context: JsonObject,

    /**
     * 快速操作对象
     */
    val operation: JsonObject
) : AbstractPersonalRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/.handle_quick_operation"
}
