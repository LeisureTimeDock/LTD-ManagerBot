
package top.r3944realms.ltdmanager.napcat.event.file

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * DeleteGroupFolder事件
 * @property data 响应数据
 */
@Serializable
data class DeleteGroupFolderEvent(
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
) : AbstractFileEvent(status0, retcode0, message0, wording0, echo0) {
    @Serializable
    data class Data (
        val clientWording: String,
        val retCode: Double,
        val retMsg: String
    )

    override fun subtype(): String {
        return "delete_group_folder"
    }
}
