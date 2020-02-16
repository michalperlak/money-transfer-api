package pl.michalperlak.moneytransfer.app

import arrow.core.Either
import arrow.core.getOrElse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.fail
import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Currency.PLN
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.core.domain.OwnerId
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.error.TransactionError
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryTransactionsRepository
import pl.michalperlak.moneytransfer.repo.TransactionsRepository
import reactor.core.publisher.Mono
import java.util.UUID

fun assertError(expected: TransactionError, result: Mono<Either<TransactionError, TransactionDto>>) {
    when (val res = result.block()!!) {
        is Either.Left -> Assertions.assertEquals(expected, res.a)
        is Either.Right -> fail { "Expected error but was: ${res.b}" }
    }
}

fun assertResult(result: Mono<Either<TransactionError, TransactionDto>>, assertions: (TransactionDto) -> Unit) {
    when (val res = result.block()!!) {
        is Either.Left -> fail { "Expected result, but was error: ${res.a}" }
        is Either.Right -> assertions(res.b)
    }
}

fun createTransactionsService(accountsRepository: AccountsRepository = InMemoryAccountsRepository(),
                              transactionsRepository: TransactionsRepository = InMemoryTransactionsRepository()) =
        TransactionsService(accountsRepository, transactionsRepository)

fun createAccount(accountId: AccountId, initialBalance: Money = Money.ZERO): Account = Account(
        id = accountId,
        currency = PLN,
        ownerId = ownerId()
).apply { deposit(initialBalance) }

fun ownerId(): OwnerId =
        OwnerId
                .of(UUID.randomUUID().toString())
                .getOrElse { throw IllegalStateException() }
