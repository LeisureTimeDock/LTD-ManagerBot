
package top.r3944realms.ltdmanager.napcat.requests.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.r3944realms.ltdmanager.napcat.data.ID

/**
 * SetGroupAddOption请求
 */
@Serializable
data class SetGroupAddOptionRequest(
    @SerialName("add_type")
    val addType: String,

    @SerialName("group_answer")
    val groupAnswer: String? = null,

    @SerialName("group_id")
    val groupId: ID,

    @SerialName("group_question")
    val groupQuestion: String? = null
) : AbstractGroupRequest() {
    override fun toJSON(): String = Json.encodeToString(this)

    override fun path(): String = "/set_group_add_option"
}
