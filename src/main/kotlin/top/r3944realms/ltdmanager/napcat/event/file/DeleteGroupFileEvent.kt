
package top.r3944realms.ltdmanager.napcat.event.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * DeleteGroupFile事件
 * @property data 响应数据
 */
@Serializable
data class DeleteGroupFileEvent(
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
        val errMsg: String,
        val result: Double,
        val transGroupFileResult: TransGroupFileResult
    )

    @Serializable
    data class TransGroupFileResult (
        @SerialName("failFileIdList")
        val failFileIDList: List<String>,

        val result: Result,

        @SerialName("successFileIdList")
        val successFileIDList: List<String>
    )

    @Serializable
    data class Result (
        val clientWording: String,
        val retCode: Double,
        val retMsg: String
    )
    override fun subtype(): String {
        return "delete_group_file"
    }
}
