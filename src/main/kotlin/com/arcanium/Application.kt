package com.arcanium

import com.arcanium.auth.controller.AuthController
import com.arcanium.auth.domain.model.TokenConfig
import com.arcanium.auth.domain.usecase.AuthUseCases
import com.arcanium.auth.router.configureAuthRouting
import com.arcanium.plugins.configureKoin
import com.arcanium.plugins.configureMonitoring
import com.arcanium.plugins.configureSecurity
import com.arcanium.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import org.koin.ktor.ext.inject
import kotlin.random.Random

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureKoin()

    val authController by inject<AuthController>()
    val tokenConfig by inject<TokenConfig>()

    configureSecurity(tokenConfig)
    configureAuthRouting(authController = authController)
    configureSerialization()
    configureMonitoring()
    val transactionObserver = object : Observer {
        override fun onTransactionEvent(transaction: Transaction) {
            // TODO - create a push noti
            Printer.printNoticeable(transaction)
        }
    }
    val bankingApi = BankingApi(transactionObserver)
    bankingApi.start()
}

data class Transaction(
    val id: Int,
    val amount: Int
)

data class BankCard(
    val cardNum: Int,
    val balance: Int,
    val isCredit: Boolean,
    val transactions: MutableList<Transaction>
)


object Printer {
    fun <T> printNoticeable(value: T) {
        println("\n\n\n\n*************** TRANSACTION **************\n ${value.toString()}\n\n\n\n")
    }
}

interface Observer {
    fun onTransactionEvent(transaction: Transaction)
}

@OptIn(DelicateCoroutinesApi::class)
class BankingApi constructor(
    private val observer: Observer
) {
    private val bankCardList: MutableList<BankCard> = mutableListOf(
        BankCard(
            cardNum = 0,
            balance = 23452,
            isCredit = false,
            transactions = mutableListOf(
                Transaction(
                    id = 12,
                    amount = -100
                ),
                Transaction(
                    id = 13,
                    amount = 138000
                )
            )
        ),
        BankCard(
            cardNum = 0,
            balance = 23452,
            isCredit = false,
            transactions = mutableListOf(
                Transaction(
                    id = 45,
                    amount = -1030
                ),
                Transaction(
                    id = 46,
                    amount = 1560
                )
            )
        )

    )

    fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                delay(2000L)
                val clientToUpdate = Random.nextInt(bankCardList.size) // number of clients registered to bank
                bankCardList[clientToUpdate].transactions.add(Transaction(id = bankCardList[clientToUpdate].transactions.lastIndex + 1, amount = Random.nextInt(-100, 100)))
                bankCardList.add(
                    index = clientToUpdate, element = bankCardList[clientToUpdate]
                        .copy(
                            cardNum = 0,
                            balance = 0,
                            isCredit = false
                        )
                )
                observer.onTransactionEvent(bankCardList[clientToUpdate].transactions.last())
            }
        }
    }
}