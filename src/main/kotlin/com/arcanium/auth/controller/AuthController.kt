package com.arcanium.auth.controller

import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.io.ResponseModel
import com.arcanium.auth.domain.io.TokenResponse
import com.arcanium.auth.domain.io.UsernameResponse
import com.arcanium.auth.domain.usecase.AuthUseCases
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

class AuthController(
    private val authUseCases: AuthUseCases
) {

    suspend fun testApi(call: ApplicationCall) {
        call.respond(
            status = HttpStatusCode.OK,
            message = "Api is A-okay"
        )
    }

    suspend fun signUp(
        call: ApplicationCall,
        request: AuthRequest
    ) {
        val response = authUseCases.signUp(authRequest = request)
        call.respond(
            status = response.httpStatusCode,
            message = response.message ?: ResponseModel.nullMessage
        )
    }

    suspend fun signIn(
        call: ApplicationCall,
        request: AuthRequest
    ) {
        val response = authUseCases.signIn(authRequest = request)
        if (response.data == null) {
            call.respond(
                status = response.httpStatusCode,
                message = response.message ?: ResponseModel.nullMessage
            )
        } else {
            call.respond(
                status = response.httpStatusCode,
                message = TokenResponse(token = response.data)
            )
        }
    }

    suspend fun authenticate(
        call: ApplicationCall
    ) {
        call.respond(
            status = HttpStatusCode.OK,
            message = "Auth is good."
        )
    }

    suspend fun getUser(
        call: ApplicationCall
    ) {
        val principle = call.principal<JWTPrincipal>()
        val userId = principle?.getClaim("userId", String::class)
        val response = authUseCases.getUser(userId)
        if (response.data == null) {
            call.respond(
                status = response.httpStatusCode,
                message = response.message ?: ResponseModel.nullMessage
            )
        } else {
            call.respond(
                status = response.httpStatusCode,
                message = UsernameResponse(username = response.data)
            )
        }
    }
}