package com.arcanium.auth.domain.usecase

import com.arcanium.auth.data.entity.SaltedHash
import com.arcanium.auth.data.entity.TokenClaim
import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.io.AuthResponse
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.domain.service.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.apache.commons.codec.digest.DigestUtils

class SignInUseCase(
    private val userDataRepository: UserDataRepository,
    private val hashingService: HashingService,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) {
    suspend operator fun invoke(
        request: AuthRequest,
        call: ApplicationCall
    ) {
        val user = userDataRepository.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return
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
            return
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