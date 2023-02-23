package com.arcanium.auth.controller

import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.usecase.SignInUseCase
import com.arcanium.auth.domain.usecase.SignUpUseCase
import com.arcanium.auth.domain.usecase.TestApi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Checks to see whether client side token is still legit
fun Route.authenticate() {
    authenticate {
        get(path = "authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getUsername(
    userDataRepository: UserDataRepository
) {
    authenticate {
        post(path = "user") {
            val principle = call.principal<JWTPrincipal>()
            val userId = principle?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.Conflict, "No user id found")
                return@post
            }
            val user = userDataRepository.getUserByUserId(userId)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, "No user for user id $userId found")
                return@post
            }
            call.respond(HttpStatusCode.OK, user.userName)
        }
    }
}

