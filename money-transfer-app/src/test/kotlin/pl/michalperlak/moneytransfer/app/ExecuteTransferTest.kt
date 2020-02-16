package pl.michalperlak.moneytransfer.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionType.TRANSFER
import pl.michalperlak.moneytransfer.error.TransferError.INVALID_AMOUNT
import pl.michalperlak.moneytransfer.error.TransferError.INVALID_DEST_ACCOUNT
import pl.michalperlak.moneytransfer.error.TransferError.INVALID_SOURCE_ACCOUNT
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository

internal class ExecuteTransferTest {

    @Test
    fun `should return error when amount is invalid`() {
        // given
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "-100.00",
                sourceAccountId = AccountId.generate().toString(),
                destinationAccountId = AccountId.generate().toString()
        )
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_AMOUNT, result)
    }

    @Test
    fun `should return error when source account id is invalid`() {
        // given
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "100.00",
                sourceAccountId = "12345",
                destinationAccountId = AccountId.generate().toString()
        )
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_SOURCE_ACCOUNT, result)
    }

    @Test
    fun `should return error when source account is missing`() {
        // given
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "100.00",
                destinationAccountId = AccountId.generate().toString()
        )
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_SOURCE_ACCOUNT, result)
    }

    @Test
    fun `should return error when source account not found`() {
        // given
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "100.00",
                sourceAccountId = AccountId.generate().toString(),
                destinationAccountId = AccountId.generate().toString()
        )
        val transactionsService = createTransactionsService()

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_SOURCE_ACCOUNT, result)
    }

    @Test
    fun `should return error when dest account id is invalid`() {
        // given
        val accountsRepository = InMemoryAccountsRepository()
        val sourceAccountId = AccountId.generate()
        accountsRepository
                .saveAccount(createAccount(sourceAccountId))
                .subscribe()
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "100.00",
                sourceAccountId = sourceAccountId.toString(),
                destinationAccountId = "12345"
        )
        val transactionsService = createTransactionsService(accountsRepository)

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_DEST_ACCOUNT, result)
    }

    @Test
    fun `should return empty result when source account does not exist`() {
        // given
        val accountsRepository = InMemoryAccountsRepository()
        val sourceAccountId = AccountId.generate()
        accountsRepository
                .saveAccount(createAccount(sourceAccountId))
                .subscribe()
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "100.00",
                sourceAccountId = sourceAccountId.toString(),
                destinationAccountId = AccountId.generate().toString()
        )
        val transactionsService = createTransactionsService(accountsRepository)

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertError(INVALID_DEST_ACCOUNT, result)
    }

    @Test
    fun `source account balance should be updated after successful transfer`() {
        // given
        val accountsRepository = InMemoryAccountsRepository()
        val sourceAccount = createAccount(AccountId.generate(), initialBalance = Money.of(1000))
        val destAccount = createAccount(AccountId.generate())
        accountsRepository
                .saveAccount(sourceAccount)
                .and { accountsRepository.saveAccount(destAccount) }
                .subscribe()
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "100.00",
                sourceAccountId = sourceAccount.id.toString(),
                destinationAccountId = destAccount.id.toString()
        )
        val transactionsService = createTransactionsService(accountsRepository)

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertResult(result) {
            assertEquals(Money.of(900), sourceAccount.balance)
        }
    }

    @Test
    fun `dest account balance should be updated after successful transfer`() {
        // given
        val accountsRepository = InMemoryAccountsRepository()
        val sourceAccount = createAccount(AccountId.generate(), initialBalance = Money.of(500))
        val destAccount = createAccount(AccountId.generate())
        accountsRepository
                .saveAccount(sourceAccount)
                .and { accountsRepository.saveAccount(destAccount) }
                .subscribe()
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = "500.00",
                sourceAccountId = sourceAccount.id.toString(),
                destinationAccountId = destAccount.id.toString()
        )
        val transactionsService = createTransactionsService(accountsRepository)

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertResult(result) {
            assertEquals(Money.of(500), destAccount.balance)
        }
    }

    @Test
    fun `transaction details should be returned when execution successful`() {
        // given
        val accountsRepository = InMemoryAccountsRepository()
        val sourceAccount = createAccount(AccountId.generate(), initialBalance = Money.of(200))
        val destAccount = createAccount(AccountId.generate())
        accountsRepository
                .saveAccount(sourceAccount)
                .and { accountsRepository.saveAccount(destAccount) }
                .subscribe()
        val amount = "100.00"
        val transaction = NewTransactionDto(
                type = TRANSFER,
                amount = amount,
                sourceAccountId = sourceAccount.id.toString(),
                destinationAccountId = destAccount.id.toString()
        )
        val transactionsService = createTransactionsService(accountsRepository)

        // when
        val result = transactionsService.execute(transaction)

        // then
        assertResult(result) {
            assertEquals(amount, it.amount)
            assertEquals(sourceAccount.id.toString(), it.sourceAccountId)
            assertEquals(destAccount.id.toString(), it.destinationAccountId)
            assertEquals(TRANSFER, it.type)
        }
    }
}