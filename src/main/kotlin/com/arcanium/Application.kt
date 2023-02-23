package com.arcanium

import com.arcanium.auth.controller.AuthController
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.domain.service.TokenService
import com.arcanium.auth.domain.usecase.AuthUseCases
import com.arcanium.auth.router.configureAuthRouting
import com.arcanium.plugins.configureKoin
import com.arcanium.plugins.configureMonitoring
import com.arcanium.plugins.configureSecurity
import com.arcanium.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    configureKoin()
    val authUseCases by inject<AuthUseCases>()
    val tokenService by inject<TokenService>()
    val userDataRepository by inject<UserDataRepository>()
    val hashingService by inject<HashingService>()
    val tokenConfig by inject<TokenConfig>()

    configureSecurity(tokenConfig)
    configureAuthRouting(authController = AuthController(authUseCases = authUseCases))
    configureSerialization()
    configureMonitoring()
}
