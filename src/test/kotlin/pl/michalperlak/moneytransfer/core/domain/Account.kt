package pl.michalperlak.moneytransfer.core.domain

import arrow.core.getOrElse
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun createAccount(): Account {
    val ownerId = validOwnerId()
    val currency = Currency.PLN
    val accountId = AccountId.generate()

    return Account(accountId, currency, ownerId)
}

fun validOwnerId(): OwnerId =
        OwnerId
                .of(UUID.randomUUID().toString())
                .getOrElse { throw IllegalStateException() }

fun executeConcurrently(times: Int, runnable: () -> Unit) {
    val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
    (1..times).forEach { _ -> executor.execute(runnable) }
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
}