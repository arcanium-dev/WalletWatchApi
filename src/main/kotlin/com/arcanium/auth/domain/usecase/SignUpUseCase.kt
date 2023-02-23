package com.arcanium.auth.domain.usecase

import com.arcanium.auth.data.entity.UserEntity
import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class SignUpUseCase(
    private val userDataRepository: UserDataRepository,
    private val hashingService: HashingService
) {
    suspend operator fun invoke(
        request: AuthRequest,
        call: ApplicationCall
    ) {
        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val passwordTooShort = request.password.length < 5
        if (areFieldsBlank || passwordTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return
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
            return
        }
        call.respond(HttpStatusCode.OK)
    }

}