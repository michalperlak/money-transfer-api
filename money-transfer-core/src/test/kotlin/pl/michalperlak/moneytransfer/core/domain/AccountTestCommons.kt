package pl.michalperlak.moneytransfer.core.domain

import arrow.core.getOrElse
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun createAccount(currency: Currency = Currency.PLN, initialBalance: Money = Money.ZERO): Account {
    val ownerId = validOwnerId()
    val accountId = AccountId.generate()
    return Account(accountId, currency, ownerId)
            .apply { deposit(initialBalance) }
}

fun validOwnerId(): OwnerId =
        OwnerId
                .of(UUID.randomUUID().toString())
                .getOrElse { throw IllegalStateException() }

fun totalBalance(accounts: Iterable<Account>): Money =
        accounts
                .map { it.balance }
                .reduce { left, right -> left + right }

fun executeConcurrently(times: Int, runnable: () -> Unit) {
    val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
    (1..times).forEach { _ -> executor.execute(runnable) }
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
}

fun <T> selectRandom(values: List<T>): T {
    val index = randomIndex(values)
    return values[index]
}

fun <T> selectRandomPair(values: List<T>): Pair<T, T> {
    val firstIndex = randomIndex(values)
    var secondIndex = randomIndex(values)
    while (firstIndex == secondIndex) {
        secondIndex = randomIndex(values)
    }
    return values[firstIndex] to values[secondIndex]
}

private fun randomIndex(values: List<*>): Int = Random.nextInt(0, values.size - 1)