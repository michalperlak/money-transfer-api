package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.UUID

internal class AccountIdTest {

    @Test
    fun `should return error when string value is not valid account id`() {
        // given
        val id = "1234"

        // when
        val result = AccountId.of(id)

        // then
        assertError(result)
    }

    @Test
    fun `should return correct result when string value is valid account id`() {
        // given
        val id = UUID.randomUUID().toString()

        // when
        val result = AccountId.of(id)

        // then
        assertResult(id, result)
    }

    @Test
    fun `account id equals-hashCode contract should be fulfilled`() {
        EqualsVerifier
                .forClass(AccountId::class.java)
                .verify()
    }

    private fun assertResult(expected: String, result: Either<Throwable, AccountId>) {
        when (result) {
            is Either.Left -> fail { "Expected value, but was error: ${result.a}" }
            is Either.Right -> assertEquals(expected, result.b.toString())
        }
    }

    private fun assertError(result: Either<Throwable, AccountId>) {
        when (result) {
            is Either.Right -> fail { "Expected error, but was: ${result.b}" }
        }
    }
}