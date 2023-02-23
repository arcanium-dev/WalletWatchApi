package com.arcanium.plugins

import com.arcanium.auth.controller.AuthController
import com.arcanium.auth.data.repository.MongoUserDataRepository
import com.arcanium.auth.data.service.JwtTokenService
import com.arcanium.auth.data.service.SHA256HashingService
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.repository.UserDataRepository
import com.arcanium.auth.domain.service.HashingService
import com.arcanium.auth.domain.service.TokenService
import com.arcanium.auth.domain.usecase.AuthUseCases
import com.arcanium.auth.domain.usecase.GetUserUseCase
import com.arcanium.auth.domain.usecase.SignInUseCase
import com.arcanium.auth.domain.usecase.SignUpUseCase
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            authModule,
            dbModule
        )
    }
}

val authModule = module {
    single { SignUpUseCase(get(), get()) }
    single { SignInUseCase(get(), get(), get(), get()) }
    single { AuthController(get()) }
    single { GetUserUseCase(get()) }

    single {
        AuthUseCases(
            signUp = get(),
            signIn = get(),
            getUser = get(),
        )
    }
    single {
        TokenConfig(
            issuer = System.getenv("ISSUER"),
            audience = System.getenv("AUDIENCE"),
            expiresIn = 60000L,
            secret = System.getenv("JWT_SECRET")
        )
    }
    single<TokenService> { JwtTokenService() }
    single<HashingService> { SHA256HashingService() }
}

val dbModule = module {
    single<CoroutineDatabase> {
        val mongoPassword = System.getenv("MONGO_PW")
        val dbName = "wallet-watch-01"
        KMongo.createClient(
            connectionString = "mongodb+srv://arcanium-dev:$mongoPassword@cluster0.yk7mtez.mongodb.net/$dbName?retryWrites=true&w=majority"
        )
            .coroutine
            .getDatabase(dbName)
    }
    single<UserDataRepository> { MongoUserDataRepository(get()) }
}