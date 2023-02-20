package com.arcanium

import com.arcanium.plugins.configureMonitoring
import com.arcanium.plugins.configureRouting
import com.arcanium.plugins.configureSecurity
import com.arcanium.plugins.configureSerialization
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
        connectionString = "mongodb+srv://<username>:$mongoPassword@cluster0.pqknvkf.mongodb.net/$dbName?retryWrites=true&w=majority"
    )
        .coroutine
        .getDatabase(dbName)

    configureRouting()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
}
