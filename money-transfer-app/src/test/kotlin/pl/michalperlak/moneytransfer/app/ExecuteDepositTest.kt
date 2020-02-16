package pl.michalperlak.moneytransfer.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.error.DepositError.INVALID_AMOUNT
import pl.michalperlak.moneytransfer.error.DepositError.INVALID_DESTINATION_ACCOUNT
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository

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
    fun `should return error when dest account does not exist`() {
        // given
        val transaction = NewTransactionDto(type = DEPOSIT, amount = "100.00", destinationAccountId = AccountId.generate().toString())
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_DESTINATION_ACCOUNT, result)
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
        val amount = "100.00"
        val transaction = NewTransactionDto(type = DEPOSIT, amount = amount, destinationAccountId = accountId.toString())
        val accountsRepository = InMemoryAccountsRepository()
        val transactionsService = createTransactionsService(accountsRepository)
        accountsRepository
                .saveAccount(account)
                .subscribe()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertResult(result) {
            assertEquals(accountId.toString(), it.destinationAccountId)
            assertEquals(amount, it.amount)
            assertEquals(Money.of(100), account.balance)
        }
    }
}