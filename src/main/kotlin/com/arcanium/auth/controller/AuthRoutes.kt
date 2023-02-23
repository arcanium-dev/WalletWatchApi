package com.arcanium.auth.controller

import com.arcanium.auth.data.io.AuthRequest
import com.arcanium.auth.data.io.AuthResponse
import com.arcanium.auth.data.entity.UserEntity
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.data.entity.SaltedHash
import com.arcanium.auth.data.entity.TokenClaim
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.service.TokenService
import com.arcanium.auth.domain.usecase.TestApi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils

fun Route.testApi(testApi: TestApi) {
    get("/") {
        testApi.invoke(call)
    }
}

fun Route.signUp(
    hashingService: HashingService,
    userDataRepository: UserDataRepository
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
        val userEntity = UserEntity(
            userName = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataRepository.insertNewUser(userEntity)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    userDataRepository: UserDataRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataRepository.getUserByUsername(request.username)
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