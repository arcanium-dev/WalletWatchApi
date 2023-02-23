package com.arcanium

import com.arcanium.auth.data.repository.MongoUserDataRepository
import com.arcanium.plugins.configureMonitoring
import com.arcanium.auth.router.configureRouting
import com.arcanium.plugins.configureSerialization
import com.arcanium.auth.data.service.SHA256HashingService
import com.arcanium.auth.data.service.JwtTokenService
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.usecase.AuthUseCases
import com.arcanium.auth.domain.usecase.TestApi
import com.arcanium.plugins.configureSecurity
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val mongoPassword = System.getenv("MONGO_PW")
    val dbName = "wallet-watch-01"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://arcanium-dev:$mongoPassword@cluster0.yk7mtez.mongodb.net/$dbName?retryWrites=true&w=majority"
    )
        .coroutine
        .getDatabase(dbName)

    val userDataSource = MongoUserDataRepository(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureSecurity(tokenConfig)
    configureRouting(
        userDataRepository = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        authUseCases = AuthUseCases(testApi = TestApi())
    )
    configureSerialization()
    configureMonitoring()

}
