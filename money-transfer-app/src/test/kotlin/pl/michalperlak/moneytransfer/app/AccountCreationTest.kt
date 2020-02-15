package pl.michalperlak.moneytransfer.app

import arrow.core.Either
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pl.michalperlak.moneytransfer.core.domain.Currency
import pl.michalperlak.moneytransfer.dto.AccountDto
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import pl.michalperlak.moneytransfer.error.AccountCreationError
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository
import reactor.core.publisher.Mono
import java.util.*

internal class AccountCreationTest {

    @Test
    fun `should return error when owner id is invalid`() {
        // given
        val newAccountDto = NewAccountDto(ownerId = "12345", currency = Currency.PLN)
        val accountsService = createAccountsService()

        // when
        val result = accountsService.createAccount(newAccountDto)

        // then
        assertError(AccountCreationError.INVALID_OWNER_ID, result)
    }

    @Test
    fun `should create account with zero initial balance`() {
        // given
        val newAccountDto = NewAccountDto(ownerId = UUID.randomUUID().toString(), currency = Currency.PLN)
        val accountsService = createAccountsService()

        // when
        val result = accountsService.createAccount(newAccountDto)

        // then
        assertResult(result) {
            assertEquals(newAccountDto.ownerId, it.ownerId)
            assertEquals(newAccountDto.currency, it.currency)
            assertEquals("0.00", it.balance)
        }
    }

    private fun createAccountsService(): AccountsService = AccountsService(InMemoryAccountsRepository())

    private fun assertResult(result: Either<AccountCreationError, Mono<AccountDto>>, assertsCall: (AccountDto) -> Unit) {
        when (result) {
            is Either.Left -> fail { "Expected result, but was error: ${result.a}" }
            is Either.Right -> assertsCall(result.b.block()!!)
        }
    }

    private fun assertError(expected: AccountCreationError, result: Either<AccountCreationError, Mono<AccountDto>>) {
        when (result) {
            is Either.Right -> fail { "Expected error, but was: ${result.b}" }
            is Either.Left -> assertEquals(expected, result.a)
        }
    }
}