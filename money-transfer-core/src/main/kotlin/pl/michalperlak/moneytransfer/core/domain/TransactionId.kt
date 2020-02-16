package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.util.of
import java.util.UUID

data class TransactionId internal constructor(private val id: UUID) {
    override fun toString(): String = id.toString()

    companion object {
        fun of(id: String): Either<Throwable, TransactionId> =
                Either.of { TransactionId(UUID.fromString(id)) }

        fun generate(): TransactionId = TransactionId(UUID.randomUUID())
    }
}