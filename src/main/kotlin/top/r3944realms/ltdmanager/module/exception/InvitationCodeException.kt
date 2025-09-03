package top.r3944realms.ltdmanager.module.exception

/**
 * 自定义异常类
 */
sealed class InvitationCodeException(message: String) : Exception(message) {

    // 使用 public 构造函数
    class QuantityMismatchException(
        val expectedCount: Int,
        val actualCount: Int
    ) : InvitationCodeException("数量不一致: 期望 $expectedCount, 实际 $actualCount")

    // 添加其他类型的异常
    class DatabaseException(message: String) : InvitationCodeException(message)

    class NetworkException(message: String) : InvitationCodeException(message)
    class ApiFailureException(message: String) : InvitationCodeException(message)
}