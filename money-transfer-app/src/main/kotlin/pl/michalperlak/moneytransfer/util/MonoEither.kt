package pl.michalperlak.moneytransfer.util

import arrow.core.Either
import arrow.core.Either.Companion.left
import reactor.core.publisher.Mono

fun <A, B> Either<A, Mono<B>>.extractMono(): Mono<Either<A, B>> =
    when (this) {
        is Either.Left -> Mono.just(left(a))
        is Either.Right -> b.map { Either.right(it) }
    }

fun <A, B> errorValue(value: A): Either<A, B> = left(value)