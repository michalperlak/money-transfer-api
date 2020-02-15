package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.util.of
import java.util.UUID

class AccountId private constructor(id: UUID) : ID(id) {
    companion object {
        fun of(id: String): Either<Throwable, AccountId> =
                Either.of { AccountId(UUID.fromString(id)) }

        fun generate(): AccountId = AccountId(UUID.randomUUID())
    }
}