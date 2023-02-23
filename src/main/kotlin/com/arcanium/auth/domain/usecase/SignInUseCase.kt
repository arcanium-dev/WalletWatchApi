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
        request: AuthRequest
    ): ResponseModel<String> {
        val userEntity = userDataRepository.getUserByUsername(request.username)

        if (userEntity == null) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "Incorrect username or password."
            )
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = userEntity.password,
                salt = userEntity.salt
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
                value = userEntity.id.toString()
            )
        )

        return ResponseModel(
            httpStatusCode = HttpStatusCode.OK,
            data = token,
            message = "Sign in successful."
        )
    }
}