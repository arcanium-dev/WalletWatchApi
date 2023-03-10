package com.arcanium.auth.domain.usecase

import com.arcanium.auth.domain.io.ResponseModel
import com.arcanium.auth.domain.repository.UserDataRepository
import com.google.common.truth.Truth.assertThat
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetUserUseCaseTest {
    private val userDataRepository: UserDataRepository = mockk()
    private val userId = "testUserId"
    private val getUserUseCase = GetUserUseCase(userDataRepository)

    @Test
    fun `invoke should return error when userId is null`() = runBlocking {
        // Given
        val expectedResponse = ResponseModel<String>(
            httpStatusCode = HttpStatusCode.Conflict,
            data = null,
            message = "Invalid user."
        )

        // When
        val actualResponse = getUserUseCase(null)

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `invoke should return error when no user is found for the given userId`() = runBlocking {
        // Given
        val expectedResponse = ResponseModel<String>(
            httpStatusCode = HttpStatusCode.Conflict,
            data = null,
            message = "No user for user id $userId found."
        )
        coEvery { userDataRepository.getUserByUserId(userId) } returns null

        // When
        val actualResponse = getUserUseCase(userId)

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `invoke should return success when user is found for the given userId`() = runBlocking {
        // Given
        val expectedResponse = ResponseModel<String>(
            httpStatusCode = HttpStatusCode.OK,
            data = userId,
            message = null
        )
        coEvery { userDataRepository.getUserByUserId(userId) } returns mockk()

        // When
        val actualResponse = getUserUseCase(userId)

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }
}