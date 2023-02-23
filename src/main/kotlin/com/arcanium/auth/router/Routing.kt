package com.arcanium.auth.router

import com.arcanium.auth.controller.AuthController
import com.arcanium.auth.domain.io.AuthRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Router of the Auth layer.
 * Includes public as well as private endpoints.
 * Performs authentication for private endpoints.
 */
fun Application.configureAuthRouting(
    authController: AuthController
) {
    routing {

        get(path = "/") {
            authController.testApi(call)
        }

        post("sign-up") {
            val request = getRequestIfNotNull<AuthRequest>(call) ?: return@post
            authController.signUp(
                call = call,
                request = request
            )
        }

        post("sign-in") {
            val request = getRequestIfNotNull<AuthRequest>(call) ?: return@post
            authController.signIn(
                call = call,
                request = request
            )
        }

        authenticate {
            get(path = "authenticate") {
                authController.authenticate(call)
            }
        }

        authenticate {
            post(path = "get-user") {
                authController.getUser(call)
            }
        }
    }
}

/**
 * Gets the authentication request after verifying that the payload coming from client side is in correct format to be deserialized on server side.
 * Should the request payload be invalid/null, this function returns an HTTP 400 error.
 * @param call ApplicationCall made by client.
 * @return T - The deserialized instance of the payload.
 */
suspend inline fun <reified T : Any> getRequestIfNotNull(call: ApplicationCall): T? {
    return runCatching { call.receiveNullable<T>() }
        .onFailure { call.respond(HttpStatusCode.BadRequest) }
        .getOrNull()
}