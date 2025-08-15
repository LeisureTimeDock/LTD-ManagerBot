package top.r3944realms.ltdmanager.napcat.requests

/**
 * 带优先级的消息封装
 * @property request 原始请求
 * @property priority 优先级数值(越大优先级越高)
 */
data class PrioritizedRequest(
    val request: NapCatRequest,
    val priority: Int = DEFAULT_PRIORITY,
) :Comparable<PrioritizedRequest> {
    companion object {
        const val HIGH_PRIORITY = 1000
        const val DEFAULT_PRIORITY = 500
        const val LOW_PRIORITY = 100
    }
    override fun compareTo(other: PrioritizedRequest): Int {
        return compareValuesBy(other, this,
            { it.priority },
            { it.request.createTime }) // 优先级相同则先创建的优先
    }
}