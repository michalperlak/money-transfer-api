package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.util.of
import java.util.UUID

class OwnerId private constructor(id: UUID) : ID(id) {
    companion object {
        fun of(id: String): Either<Throwable, OwnerId> =
                Either.of { OwnerId(UUID.fromString(id)) }
    }
}