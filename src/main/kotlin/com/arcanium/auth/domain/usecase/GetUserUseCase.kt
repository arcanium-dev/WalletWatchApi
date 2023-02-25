package com.arcanium.auth.domain.usecase

import com.arcanium.auth.domain.io.ResponseModel
import com.arcanium.auth.domain.repository.UserDataRepository
import io.ktor.http.*

class GetUserUseCase(
    private val userDataRepository: UserDataRepository
) {
    suspend operator fun invoke(
        userId: String?
    ): ResponseModel<String> {
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