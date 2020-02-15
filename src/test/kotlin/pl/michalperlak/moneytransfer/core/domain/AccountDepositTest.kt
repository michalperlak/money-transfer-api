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


}