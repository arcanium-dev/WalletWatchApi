package com.arcanium.auth.domain.usecase

import com.arcanium.auth.domain.io.AuthRequest
import com.arcanium.auth.domain.model.SaltedHash
import com.arcanium.auth.domain.model.TokenClaim
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.model.User
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.domain.service.TokenService
import com.google.common.truth.Truth.assertThat
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
class SignInUseCaseTest {

    private lateinit var userDataRepository: UserDataRepository
    private lateinit var hashingService: HashingService
    private lateinit var tokenService: TokenService
    private lateinit var tokenConfig: TokenConfig
    private lateinit var signInUseCase: SignInUseCase

    @Before
    fun setUp() {
        userDataRepository = mockk()
        hashingService = mockk()
        tokenService = mockk()
        tokenConfig = mockk()
        signInUseCase = SignInUseCase(userDataRepository, hashingService, tokenService, tokenConfig)
    }

    @Test
    fun `invoke should return error when user is not found`() = runBlocking {
        // Given
        val authRequest = AuthRequest("username", "password")
        coEvery { userDataRepository.getUserByUsername(authRequest.username) } returns null

        // When
        val response = signInUseCase(authRequest)

        // Then
        assertThat(response.httpStatusCode).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.data).isNull()
        assertThat(response.message).isEqualTo("Incorrect username or password. 01")
    }

    @Test
    fun `invoke should return error when password is incorrect`() = runBlocking {
        // Given
        val authRequest = AuthRequest("username", "password")
        val user = User(
            username = "id",
            password = "username",
            salt = "hashed_password",
            id = "salt"
        )
        coEvery { userDataRepository.getUserByUsername(authRequest.username) } returns user
        coEvery { hashingService.verify(authRequest.password, SaltedHash(hash = "hashed_password", salt = "salt")) } returns false

        // When
        val response = signInUseCase(authRequest)

        // Then
        assertThat(response.httpStatusCode).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.data).isNull()
        assertThat(response.message).isEqualTo("Incorrect username or password.")
    }

    @Test
    fun `invoke should return token when sign in is successful`() = runBlocking {
        // Given
        val authRequest = AuthRequest("username", "password")
        val user = User("id", "username", "hashed_password", "salt")
        val token = "token"
        coEvery { userDataRepository.getUserByUsername(authRequest.username) } returns user
        coEvery { hashingService.verify(authRequest.password, SaltedHash("hashed_password", "salt")) } returns true
        coEvery { tokenService.generate(tokenConfig, TokenClaim("userId", "id")) } returns token

        // When
        val response = signInUseCase(authRequest)

        // Then
        assertThat(response.httpStatusCode).isEqualTo(HttpStatusCode.OK)
        assertThat(response.data).isEqualTo(token)
        assertThat(response.message).isEqualTo("Sign in successful.")
    }

    @Test
    fun `invoke should return conflict error when password is incorrect`() = runBlocking {
        // Given
        val authRequest = AuthRequest("username", "password123")
        val user = User("userId", "username", "salt", "invalidHash")
        coEvery { userDataRepository.getUserByUsername(authRequest.username) } returns user
        coEvery { hashingService.verify(any(), any()) } returns false

        // When
        val result = signInUseCase.invoke(authRequest)

        // Then
        assertThat(result.httpStatusCode).isEqualTo(HttpStatusCode.Conflict)
        assertThat(result.data).isNull()
        assertThat(result.message).isEqualTo("Incorrect username or password.")
        coVerify { userDataRepository.getUserByUsername(authRequest.username) }
        coVerify { hashingService.verify(authRequest.password, SaltedHash(user.password, user.salt)) }
        confirmVerified(userDataRepository, hashingService)
    }
}
