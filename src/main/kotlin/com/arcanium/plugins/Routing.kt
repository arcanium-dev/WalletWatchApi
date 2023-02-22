package com.arcanium.plugins

import com.arcanium.*
import com.arcanium.data.user.UserDataSource
import com.arcanium.security.hashing.HashingService
import com.arcanium.security.token.TokenConfig
import com.arcanium.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        testApi()
        signIn(
            userDataSource = userDataSource,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )
        signUp(
            hashingService = hashingService,
            userDataSource = userDataSource
        )
        authenticate()
        getSecretInfo()
        getUsername(userDataSource)
    }
}
