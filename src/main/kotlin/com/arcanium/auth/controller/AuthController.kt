package com.arcanium.auth.controller

import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.usecase.AuthUseCases
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import javax.xml.transform.OutputKeys

class AuthController(
    private val authUseCases: AuthUseCases
) {

    suspend fun testApi(call: ApplicationCall) {
        authUseCases.testApi(call)
    }

    suspend fun signUp(
        call: ApplicationCall,
        request: AuthRequest
    ) {
        authUseCases.signIn(call = call, request = request)
    }

    suspend fun signIn(
        call: ApplicationCall,
        request: AuthRequest
    ) {
        authUseCases.signIn(
            request = request,
            call = call
        )
    }

    suspend fun authenticate(
        call: ApplicationCall
    ) {
        call.respond(HttpStatusCode.OK, "auth is good")
    }
}