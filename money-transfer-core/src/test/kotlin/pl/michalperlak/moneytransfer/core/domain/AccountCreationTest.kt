package pl.michalperlak.moneytransfer.core.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AccountCreationTest {

    @Test
    fun `should create account with zero balance`() {
        // given
        val ownerId = validOwnerId()
        val currency = Currency.PLN
        val accountId = AccountId.generate()

        // when
        val account = Account(accountId, currency, ownerId)

        // then
        assertEquals(Money.ZERO, account.balance)
    }
}