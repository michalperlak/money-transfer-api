package pl.michalperlak.moneytransfer.web.json

import arrow.core.Either

interface JsonMapper {
    fun <T : Any> read(value: String, targetClass: Class<T>): Either<Throwable, T>
    fun <T : Any> write(value: T): String
}

inline fun <reified T : Any> JsonMapper.read(value: String): Either<Throwable, T> {
    val targetClass = T::class.java
    return read(value, targetClass)
}