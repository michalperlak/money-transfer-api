package pl.michalperlak.moneytransfer.core.domain

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.math.BigDecimal

internal class MoneyTest {

    @Test
    fun `should format value with two decimal places`() {
        // given
        val money = Money.of(20)

        // when
        val formatted = money.asString()

        // then
        assertEquals("20.00", formatted)
    }

    @Test
    fun `equality operator should return true when represented values are equal`() {
        // given
        val left = Money.of(BigDecimal.valueOf(10))
        val right = Money.of("10.0000")

        // when
        val result = left == right

        // then
        assertTrue(result)
    }

    @Test
    fun `plus operator should return value representing the sum of left and right`() {
        // given
        val left = Money.of(5)
        val right = Money.of(12)

        // when
        val sum = left + right

        // then
        assertEquals(Money.of(17), sum)
    }

    @Test
    fun `minus operator should throw exception when right value is greater than left`() {
        // given
        val left = Money.of(2)
        val right = Money.of(5)

        // when
        val operation = { left - right }

        // then
        assertThrows<IllegalArgumentException> { operation.invoke() }
    }

    @Test
    fun `minus operator should return value representing the diff between left and right`() {
        // given
        val left = Money.of(10)
        val right = Money.of(6)

        // when
        val diff = left - right

        // then
        assertEquals(Money.of(4), diff)
    }

    @Test
    fun `greater than operator should return false when values are equal`() {
        // given
        val left = Money.ZERO
        val right = Money.of(0)

        // when
        val result = left > right

        // then
        assertFalse(result)
    }

    @Test
    fun `greater than operator should return true when left value is greater`() {
        // given
        val left = Money.of(2)
        val right = Money.of(1)

        // when
        val result = left > right

        // then
        assertTrue(result)
    }

    @Test
    fun `greater than operator should return false when right value is greater`() {
        // given
        val left = Money.of(2)
        val right = Money.of(5)

        // when
        val result = left > right

        // then
        assertFalse(result)
    }

    @Test
    fun `greater than or equal operator should return true when values are equal`() {
        // given
        val left = Money.of("123")
        val right = Money.of(123)

        // when
        val result = left >= right

        // then
        assertTrue(result)
    }

    @Test
    fun `greater than or equal operator should return true when left value is greater`() {
        // given
        val left = Money.of("123.567")
        val right = Money.of(123)

        // when
        val result = left >= right

        // then
        assertTrue(result)
    }

    @Test
    fun `greater than or equal operator should return false when right value is greater`() {
        // given
        val left = Money.of(50)
        val right = Money.of(100)

        // when
        val result = left >= right

        // then
        assertFalse(result)
    }

    @Test
    fun `equals-hashCode contract should be fulfilled`() {
        EqualsVerifier
                .forClass(Money::class.java)
                .verify()
    }
}