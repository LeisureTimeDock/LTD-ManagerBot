package top.r3944realms.ltdmanager.core.client.response

import io.ktor.http.*

interface IResponse {
    val httpStatusCode: HttpStatusCode
    val createTime: Long
}