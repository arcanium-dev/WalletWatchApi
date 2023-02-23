package com.arcanium.auth.router

import com.arcanium.auth.controller.*
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.service.TokenService
import com.arcanium.auth.domain.usecase.AuthUseCases
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataRepository: UserDataRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    authUseCases: AuthUseCases
) {
    routing {
        testApi(testApi = authUseCases.testApi)
        signIn(
            userDataRepository = userDataRepository,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )
        signUp(
            hashingService = hashingService,
            userDataRepository = userDataRepository
        )
        authenticate()
        getUsername(userDataRepository)
    }
}