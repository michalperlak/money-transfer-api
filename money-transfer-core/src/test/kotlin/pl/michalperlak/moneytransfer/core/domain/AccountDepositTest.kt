package pl.michalperlak.moneytransfer.core.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test

internal class AccountDepositTest {

    @Test
    fun `account balance should increase after deposit`() {
        // given
        val account = createAccount()
        assumeTrue(Money.ZERO == account.balance)
        val depositValue = Money.of(1000)

        // when
        account.deposit(depositValue)

        // then
        assertEquals(depositValue, account.balance)
    }

    @Test
    fun `account deposit should be thread safe`() {
        // given
        val account = createAccount()
        assumeTrue(Money.ZERO == account.balance)

        // when
        executeConcurrently(100) {
            account.deposit(Money.of(1))
        }

        // then
        assertEquals(Money.of(100), account.balance)
    }
}