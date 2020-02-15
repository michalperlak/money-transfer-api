package pl.michalperlak.moneytransfer.app

import arrow.core.getOrHandle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Currency
import pl.michalperlak.moneytransfer.core.domain.Currency.EUR
import pl.michalperlak.moneytransfer.dto.AccountDto
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import java.lang.IllegalStateException

internal class AccountRetrievalTest {

    @Test
    fun `should return empty result when account does not exist`() {
        // given
        val accountsService = createAccountsService()

        // when
        val result = accountsService.getAccount(AccountId.generate().toString())

        // then
        assertNull(result.block())
    }

    @Test
    fun `should return result with account data when account exists`() {
        // given
        val accountsService = createAccountsService()
        val account = addAccount(accountsService, ownerId = validOwnerId(), currency = EUR)

        // when
        val result = accountsService.getAccount(account.id)

        // then
        val foundAccount = result.block()!!
        assertEquals(account, foundAccount)
    }

    private fun addAccount(accountsService: AccountsService, ownerId: String, currency: Currency): AccountDto {
        val newAccountDto = NewAccountDto(ownerId = ownerId, currency = currency)
        return accountsService
                .createAccount(newAccountDto)
                .getOrHandle { throw IllegalStateException("Error creating account: $it") }
                .block()!!
    }
}