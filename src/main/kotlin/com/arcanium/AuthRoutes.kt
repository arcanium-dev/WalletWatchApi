package com.arcanium

import com.arcanium.data.request.AuthRequest
import com.arcanium.data.responses.AuthResponse
import com.arcanium.data.user.User
import com.arcanium.data.user.UserDataSource
import com.arcanium.security.hashing.HashingService
import com.arcanium.security.hashing.SaltedHash
import com.arcanium.security.token.TokenClaim
import com.arcanium.security.token.TokenConfig
import com.arcanium.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils

fun Route.testApi() {
    get("/") {
        call.respond("Api is up and running!")
    }
}

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post(path = "signup") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "break 1")
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val passwordTooShort = request.password.length < 5
        if (areFieldsBlank || passwordTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            userName = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertNewUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            call.respond(HttpStatusCode.Conflict, "Incorrect password \n Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}


// Checks to see whether client side token is still legit
fun Route.authenticate() {
    authenticate {
        get(path = "authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

// gets the userId from a valid token
fun Route.getSecretInfo() {
    authenticate {
        get(path = "secret") {
            val principle = call.principal<JWTPrincipal>()
            val userId = principle?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "$userId")
        }
    }
}