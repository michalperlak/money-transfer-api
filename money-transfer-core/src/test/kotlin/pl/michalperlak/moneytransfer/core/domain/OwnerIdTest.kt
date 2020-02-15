package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.UUID

internal class OwnerIdTest {

    @Test
    fun `should return error when string value is not valid owner id`() {
        // given
        val id = "ABC"

        // when
        val result = OwnerId.of(id)

        // then
        assertError(result)
    }

    @Test
    fun `should return correct result when string value is valid id`() {
        // given
        val id = UUID.randomUUID().toString()

        // when
        val result = OwnerId.of(id)

        // then
        assertResult(id, result)
    }

    @Test
    fun `owner id equals-hashCode contract should be fulfilled`() {
        EqualsVerifier
                .forClass(OwnerId::class.java)
                .verify()
    }

    private fun assertResult(expected: String, result: Either<Throwable, OwnerId>) {
        when (result) {
            is Either.Left -> fail { "Expected value, but was error: ${result.a}" }
            is Either.Right -> assertEquals(expected, result.b.toString())
        }
    }

    private fun assertError(result: Either<Throwable, OwnerId>) {
        when (result) {
            is Either.Right -> fail { "Expected error, but was: ${result.b}" }
        }
    }

}