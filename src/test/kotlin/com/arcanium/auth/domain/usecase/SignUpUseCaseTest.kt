package com.arcanium.auth.domain.usecase

import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.model.SaltedHash
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class SignUpUseCaseTest {

    private lateinit var useCase: SignUpUseCase
    private lateinit var userDataRepository: UserDataRepository
    private lateinit var hashingService: HashingService

    @Before
    fun setUp() {
        userDataRepository = mockk()
        hashingService = mockk()
        useCase = SignUpUseCase(userDataRepository, hashingService)
    }

    @Test
    fun `valid sign up details returns OK response`() = runBlocking {
        // Given
        val authRequest = AuthRequest("username", "password123")
        val saltedHash = SaltedHash("salt", "hash")
        coEvery { hashingService.generateSaltedHash(authRequest.password) } returns saltedHash
        coEvery { userDataRepository.insertNewUser(any()) } returns true

        // When
        val response = useCase.invoke(authRequest)

        // Then
        assertThat(HttpStatusCode.OK).isEqualTo(response.httpStatusCode)
        assertThat("User registered.").isEqualTo(response.message)
        assertThat(response.data).isNull()
    }

    @Test
    fun `when sign up details are invalid, return Conflict response`() = runBlocking {
        // Given
        val authRequest = AuthRequest("", "1234")

        // When
        val response = useCase.invoke(authRequest)

        // Then
        assertThat(response.httpStatusCode).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.data).isNull()
        assertThat(response.message).isEqualTo("Invalid sign up details.")
    }

    @Test
    fun `when user is already registered, return Conflict response`() = runBlocking {
        // Given
        val authRequest = AuthRequest("username", "password123")
        val saltedHash = SaltedHash("salt", "hash")
        coEvery { hashingService.generateSaltedHash(authRequest.password) } returns saltedHash
        coEvery { userDataRepository.insertNewUser(any()) } returns false

        // When
        val response = useCase.invoke(authRequest)

        // Then
        assertThat(response.httpStatusCode).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.data).isNull()
        assertThat(response.message).isEqualTo("This user is already registered.")
    }
}