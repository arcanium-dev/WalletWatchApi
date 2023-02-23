package com.arcanium.auth.domain.usecase

/**
 * Aggregates all use cases into one data class to reduce boiler plate
 */
data class AuthUseCases(
    val testApi: TestApi,
    val signUp: SignUpUseCase,
    val signIn: SignInUseCase
)
