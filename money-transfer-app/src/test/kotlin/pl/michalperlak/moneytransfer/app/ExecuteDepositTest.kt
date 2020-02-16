package pl.michalperlak.moneytransfer.app

import arrow.core.Either
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.core.domain.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.error.DepositError.INVALID_AMOUNT
import pl.michalperlak.moneytransfer.error.DepositError.INVALID_DESTINATION_ACCOUNT
import pl.michalperlak.moneytransfer.error.TransactionError
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository
import reactor.core.publisher.Mono

internal class ExecuteDepositTest {

    @Test
    fun `should return error when dest account id is invalid`() {
        // given
        val transaction = NewTransactionDto(type = DEPOSIT, amount = "100.00", destinationAccountId = "12345")
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_DESTINATION_ACCOUNT, result)
    }

    @Test
    fun `should return empty result when dest account does not exist`() {
        // given
        val transaction = NewTransactionDto(type = DEPOSIT, amount = "100.00", destinationAccountId = AccountId.generate().toString())
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertResult(result) {
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `should return error when amount is invalid`() {
        // given
        val transaction = NewTransactionDto(type = DEPOSIT, amount = "-100.12", destinationAccountId = AccountId.generate().toString())
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_AMOUNT, result)
    }

    @Test
    fun `should execute deposit and return transaction info`() {
        // given
        val accountId = AccountId.generate()
        val account = createAccount(accountId)
        val transaction = NewTransactionDto(type = DEPOSIT, amount = "100.00", destinationAccountId = accountId.toString())
        val accountsRepository = InMemoryAccountsRepository()
        val transactionsService = createTransactionsService(accountsRepository)
        accountsRepository
                .saveAccount(account)
                .subscribe()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertResult(result) {
            assertFalse(it.isEmpty())
            assertEquals(Money.of(100), account.balance)
        }
    }

    private fun assertError(expected: TransactionError, result: Either<TransactionError, Mono<TransactionDto>>) {
        when (result) {
            is Either.Left -> assertEquals(expected, result.a)
            is Either.Right -> fail { "Expected error but was: ${result.b}" }
        }
    }

    private fun assertResult(result: Either<TransactionError, Mono<TransactionDto>>, assertions: (Mono<TransactionDto>) -> Unit) {
        when (result) {
            is Either.Left -> fail { "Expected result, but was error: ${result.a}" }
            is Either.Right -> assertions(result.b)
        }
    }
}