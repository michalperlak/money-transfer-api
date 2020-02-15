package pl.michalperlak.moneytransfer.core.domain

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
import java.util.*

class Money private constructor(
        private val value: BigDecimal
) {
    fun asString(): String = value.toPlainString()

    override fun equals(other: Any?): Boolean {
        if (other == null || this.javaClass != other.javaClass) {
            return false
        }
        val that = other as Money
        return value == that.value
    }

    override fun hashCode(): Int = Objects.hash(value)

    override fun toString(): String = "Money[${asString()}]"

    operator fun compareTo(other: Money): Int {
        return value.compareTo(other.value)
    }

    operator fun plus(other: Money): Money {
        return Money(value + other.value)
    }

    operator fun minus(other: Money): Money {
        if (value < other.value) {
            throw IllegalArgumentException("Cannot subtract greater value. Current value: $value, subtracted: ${other.value}")
        }
        return Money(value - other.value)
    }

    companion object {
        fun of(value: String) = of(BigDecimal(value))

        fun of(value: Long): Money = of(BigDecimal.valueOf(value))

        fun of(value: BigDecimal): Money = Money(value.setScale(2, HALF_EVEN))

        val ZERO = Money(BigDecimal.ZERO.setScale(2, HALF_EVEN))
    }
}