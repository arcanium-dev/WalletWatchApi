package com.arcanium.auth.domain.usecase

import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.io.ResponseModel
import com.arcanium.auth.domain.model.SaltedHash
import com.arcanium.auth.domain.model.TokenClaim
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.domain.service.TokenService
import io.ktor.http.*

class SignInUseCase(
    private val userDataRepository: UserDataRepository,
    private val hashingService: HashingService,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) {
    suspend operator fun invoke(
        authRequest: AuthRequest
    ): ResponseModel<String> {
        val user = userDataRepository.getUserByUsername(authRequest.username)

        if (user == null) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "Incorrect username or password. 01"
            )
        }

        val isValidPassword = hashingService.verify(
            value = authRequest.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "Incorrect username or password."
            )
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id
            )
        )

        return ResponseModel(
            httpStatusCode = HttpStatusCode.OK,
            data = token,
            message = "Sign in successful."
        )
    }
}