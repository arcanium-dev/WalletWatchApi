package com.arcanium.auth.domain.usecase

import com.arcanium.auth.data.entity.UserEntity
import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.io.ResponseModel
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import io.ktor.http.*

class SignUpUseCase(
    private val userDataRepository: UserDataRepository,
    private val hashingService: HashingService
) {
    suspend operator fun invoke(
        request: AuthRequest
    ): ResponseModel<Unit> {
        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val passwordTooShort = request.password.length < 5
        if (areFieldsBlank || passwordTooShort) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "Invalid sign up details."
            )
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val userEntity = UserEntity(
            userName = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataRepository.insertNewUser(userEntity)
        if (!wasAcknowledged) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "This user is already registered."
            )
        }
        return ResponseModel(
            httpStatusCode = HttpStatusCode.OK,
            data = null,
            message = "User registered."
        )
    }
}