
package top.r3944realms.ltdmanager.napcat.event.other

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.r3944realms.ltdmanager.napcat.Developing
import top.r3944realms.ltdmanager.napcat.data.CollectionSearchList

/**
 * GetCollectionList事件
 * @property data 响应数据
 */
@Developing
@Serializable
data class GetCollectionListEvent(
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

    val data: Data
) : AbstractOtherEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val collectionSearchList: CollectionSearchList,
        val errMsg: String,
        val result: Double
    )
    override fun subtype(): String {
        return "get_collection_list"
    }
}
