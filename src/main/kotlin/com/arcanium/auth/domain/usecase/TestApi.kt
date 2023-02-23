package com.arcanium.auth.domain.usecase

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class TestApi {
    suspend operator fun invoke(call: ApplicationCall) {
        call.respond(
            status = HttpStatusCode.OK,
            message = "Api is up and running!"
        )
    }
}