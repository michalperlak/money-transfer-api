package pl.michalperlak.moneytransfer.core.domain

import arrow.core.getOrElse
import java.util.*

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

