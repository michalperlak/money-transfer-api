package pl.michalperlak.moneytransfer.core.util

import arrow.core.Either
import arrow.core.nonFatalOrThrow

fun <R> Either.Companion.of(supplier: () -> R): Either<Throwable, R> {
    return try {
        right(supplier())
    } catch (t: Throwable) {
        left(t.nonFatalOrThrow())
    }
}