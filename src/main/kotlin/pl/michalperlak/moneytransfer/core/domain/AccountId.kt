package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.util.of
import java.util.*

data class AccountId internal constructor(private val id: UUID) {
    override fun toString(): String = id.toString()

    companion object {
        fun of(id: String): Either<Throwable, AccountId> =
                Either.of { AccountId(UUID.fromString(id)) }

        fun generate(): AccountId = AccountId(UUID.randomUUID())
    }
}