
package top.r3944realms.ltdmanager.napcat.request.file

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.Developing

/**
 * CleanCache请求
 */
@Developing
@Serializable
class CleanCacheRequest : AbstractFileRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/clean_cache"
}
