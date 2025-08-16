package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable

@Serializable
data class CharBoxElement (
    val charBox: CharBoxCharBox,
    val charText: String
) {
    @Serializable
    data class CharBoxCharBox (
        /**
         * 顶点坐标
         */
        val pt1: CharBoxPt,

        /**
         * 顶点坐标
         */
        val pt2: CharBoxPt,

        /**
         * 顶点坐标
         */
        val pt3: CharBoxPt,

        /**
         * 顶点坐标
         */
        val pt4: CharBoxPt
    )
    /**
     * 顶点坐标
     */
    @Serializable
    data class CharBoxPt (
        val x: String,
        val y: String
    )
}