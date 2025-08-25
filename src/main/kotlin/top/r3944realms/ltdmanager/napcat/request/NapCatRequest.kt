package top.r3944realms.ltdmanager.napcat.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 请求内容
 * @property createTime 创建时间(用于相同优先级时排序)
 */
@Serializable
abstract class NapCatRequest(
    @Transient
    open val createTime: Long = System.currentTimeMillis()
) {
    abstract fun toJSON(): String
    fun type(): String {
        return header() + path()
    }
    abstract fun path(): String
    abstract fun header(): String
}
