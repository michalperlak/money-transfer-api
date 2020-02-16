package pl.michalperlak.moneytransfer.util

import arrow.core.Either

fun <A, B, R> Either<A, B>.transform(onLeft: (A) -> R, onRight: (B) -> R): R =
        when (this) {
            is Either.Left -> onLeft(a)
            is Either.Right -> onRight(b)
        }