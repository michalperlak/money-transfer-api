package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.util.of
import java.util.UUID

data class OwnerId internal constructor(private val id: UUID) {
    override fun toString(): String = id.toString()

    companion object {
        fun of(id: String): Either<Throwable, OwnerId> =
                Either.of { OwnerId(UUID.fromString(id)) }
    }
}