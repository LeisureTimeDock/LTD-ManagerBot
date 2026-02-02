package top.r3944realms.ltdmanager.core.client.response

sealed class ResponseResult<out T: IResponse, out F: IFailedResponse> {
    data class Success<T : IResponse>(val response: T) : ResponseResult<T, Nothing>()
    data class Failure<F : IFailedResponse>(val failure: F) : ResponseResult<Nothing, F>()
    /**
     * 检查是否成功
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * 获取成功响应（如果存在）
     */
    fun getSuccessResponse(): T? = (this as? Success)?.response

    /**
     * 获取失败响应（如果存在）
     */
    fun getFailureResponse(): F? = (this as? Failure)?.failure

    /**
     * 成功时执行操作
     */
    inline fun onSuccess(action: (T) -> Unit): ResponseResult<T, F> {
        if (this is Success) action(response)
        return this
    }

    /**
     * 失败时执行操作
     */
    inline fun onFailure(action: (F) -> Unit): ResponseResult<T, F> {
        if (this is Failure) action(failure)
        return this
    }

    fun getRetResponse(): T {
        if (this is Success) {
            return response
        }
        else if (this is Failure) {
            @Suppress("UNCHECKED_CAST")
            return failure as T
        }
        throw Exception("Never Reach")
    }
}