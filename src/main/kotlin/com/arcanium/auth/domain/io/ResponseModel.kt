package com.arcanium.auth.domain.io

import io.ktor.http.*

data class ResponseModel <T: Any?> (
    val httpStatusCode: HttpStatusCode,
    val data: T?,
    val message: String?
) {
    companion object {
        const val nullMessage = "Error getting message."
    }
}
