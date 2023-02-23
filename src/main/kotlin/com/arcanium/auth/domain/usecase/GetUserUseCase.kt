package com.arcanium.auth.domain.usecase

import com.arcanium.auth.domain.io.ResponseModel
import com.arcanium.auth.domain.repository.UserDataRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class GetUserUseCase(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        call: ApplicationCall
    ): ResponseModel<String> {

        val principle = call.principal<JWTPrincipal>()
        val userId = principle?.getClaim("userId", String::class)

        if (userId == null) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "Invalid user."
            )
        }

        val user = userDataRepository.getUserByUserId(userId)
        if (user == null) {
            return ResponseModel(
                httpStatusCode = HttpStatusCode.Conflict,
                data = null,
                message = "No user for user id $userId found."
            )
        }
        return ResponseModel(
            httpStatusCode = HttpStatusCode.OK,
            data = userId,
            message = null
        )
    }
}