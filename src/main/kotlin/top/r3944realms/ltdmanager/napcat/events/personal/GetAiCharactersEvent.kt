
package top.r3944realms.ltdmanager.napcat.events.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * GetAiCharacters事件
 * @property data 响应数据
 */
@Serializable
data class GetAiCharactersEvent(
    @Transient
    val status0: Status = Status.Ok,
    @Transient
    val retcode0: Double = 0.0,
    @Transient
    val message0: String = "",
    @Transient
    val wording0: String = "",
    @Transient
    val echo0: String? = null,

    val data: List<Datum>
) : AbstractPersonalEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Datum (
        /**
         * 人物列表
         */
        val characters: List<top.r3944realms.ltdmanager.napcat.data.Character>,

        /**
         * 类型
         */
        val type: String
    )
    override fun subtype(): String {
        return "get_ai_characters"
    }
}
