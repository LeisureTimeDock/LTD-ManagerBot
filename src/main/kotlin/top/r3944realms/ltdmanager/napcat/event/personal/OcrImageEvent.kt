
package top.r3944realms.ltdmanager.napcat.event.personal

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.data.CharBoxElement

/**
 * OcrImage事件
 * @property data 响应数据
 */
@Serializable
data class OcrImageEvent(
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
    /**
     * 一个代表一行
     */
    @Serializable
    data class Datum (
        /**
         * 拆分
         */
        val charBox: List<CharBoxElement>,

        /**
         * 顶点坐标
         */
        val pt1: CharBoxElement.CharBoxPt,

        /**
         * 顶点坐标
         */
        val pt2: CharBoxElement.CharBoxPt,

        /**
         * 顶点坐标
         */
        val pt3: CharBoxElement.CharBoxPt,

        /**
         * 顶点坐标
         */
        val pt4: CharBoxElement.CharBoxPt,

        val score: String,

        /**
         * 该行文本总和
         */
        val text: String
    )
    override fun subtype(): String {
        return "ocr_image"
    }
}
