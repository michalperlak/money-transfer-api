package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.fail

internal class AccountTransferTest {

    @Test
    fun `account balances should be updated after transfer`() {
        // given
        val source = createAccount(initialBalance = Money.of(5000))
        val destination = createAccount()

        // when
        source.transfer(destination, Money.of(2000))

        // then
        assertAll(
                { assertEquals(Money.of(3000), source.balance) },
                { assertEquals(Money.of(2000), destination.balance) }
        )
    }

    @Test
    fun `should return error when accounts have different currencies`() {
        // given
        val source = createAccount(currency = Currency.PLN, initialBalance = Money.of(1000))
        val destination = createAccount(currency = Currency.EUR, initialBalance = Money.of(100))

        // when
        val result = source.transfer(destination, Money.of(500))

        // then
        assertTransferError(TransferError.INCOMPATIBLE_CURRENCIES, result)
    }

    @Test
    fun `should return error when funds on source account are insufficient`() {
        // given
        val source = createAccount(initialBalance = Money.of(100))
        val destination = createAccount()

        // when
        val result = source.transfer(destination, Money.of(200))

        // then
        assertTransferError(TransferError.INSUFFICIENT_FUNDS, result)
    }

    @Test
    fun `multiple concurrent transfers from the same source account should be thread safe`() {
        // given
        val sourceAccount = createAccount(initialBalance = Money.of(10000))
        val destAccounts = (1..5).map { createAccount() }
        val transferAmount = Money.of(10)

        // when
        executeConcurrently(100) {
            val destAccount = selectRandom(destAccounts)
            sourceAccount.transfer(destAccount, transferAmount)
        }

        // then
        val destTotal = totalBalance(destAccounts)
        assertAll(
                { assertEquals(Money.of(9000), sourceAccount.balance) },
                { assertEquals(Money.of(1000), destTotal) }
        )
    }

    @Test
    fun `multiple concurrent transfers to the same dest account should be thread safe`() {
        // given
        val sourceAccounts = (1..5).map { createAccount(initialBalance = Money.of(1000)) }
        val destAccount = createAccount()
        val transferAmount = Money.of(5)

        // when
        executeConcurrently(100) {
            val sourceAccount = selectRandom(sourceAccounts)
            sourceAccount.transfer(destAccount, transferAmount)
        }

        // then
        val sourceAccountsTotal = totalBalance(sourceAccounts)
        assertAll(
                { assertEquals(Money.of(500), destAccount.balance) },
                { assertEquals(Money.of(4500), sourceAccountsTotal) }
        )
    }

    @Test
    fun `mixed transfers from and to accounts should be thread safe`() {
        // given
        val accounts = (1..20).map { createAccount(initialBalance = Money.of(1000)) }
        val transferAmount = Money.of(5)

        // when
        executeConcurrently(200) {
            val (sourceAccount, destAccount) = selectRandomPair(accounts)
            sourceAccount.transfer(destAccount, transferAmount)
        }

        val accountsTotal = totalBalance(accounts)
        assertEquals(Money.of(20000), accountsTotal)
    }

    private fun assertTransferError(expected: TransferError, result: Either<TransferError, Transaction>) {
        when (result) {
            is Either.Left -> assertSame(expected, result.a)
            is Either.Right -> fail { "Expected transfer error: $expected, but was: ${result.b}" }
        }
    }
}