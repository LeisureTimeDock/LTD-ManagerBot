package top.r3944realms.ltdmanager.core.client.response

interface IFailedResponse : IResponse {
    val failedMessage: String
    val thrownException: Exception
        get() = Exception(failedMessage)
}