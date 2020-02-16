package pl.michalperlak.moneytransfer.app

import arrow.core.getOrElse
import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Currency.PLN
import pl.michalperlak.moneytransfer.core.domain.OwnerId
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryTransactionsRepository
import pl.michalperlak.moneytransfer.repo.TransactionsRepository
import reactor.core.publisher.Mono
import java.util.UUID

fun createTransactionsService(accountsRepository: AccountsRepository = InMemoryAccountsRepository(),
                              transactionsRepository: TransactionsRepository = InMemoryTransactionsRepository()) =
        TransactionsService(accountsRepository, transactionsRepository)

fun createAccount(accountId: AccountId): Account = Account(
        id = accountId,
        currency = PLN,
        ownerId = ownerId()
)

fun ownerId(): OwnerId =
        OwnerId
                .of(UUID.randomUUID().toString())
                .getOrElse { throw IllegalStateException() }

fun Mono<*>.isEmpty() = block() == null

