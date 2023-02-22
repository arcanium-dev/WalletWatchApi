package com.arcanium

import com.arcanium.data.user.MongoDataSource
import com.arcanium.plugins.configureMonitoring
import com.arcanium.plugins.configureRouting
import com.arcanium.plugins.configureSecurity
import com.arcanium.plugins.configureSerialization
import com.arcanium.security.hashing.SHA256HashingService
import com.arcanium.security.token.JwtTokenService
import com.arcanium.security.token.TokenConfig
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

    val userDataSource = MongoDataSource(db)
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
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )
    configureSerialization()
    configureMonitoring()

}
