package pl.michalperlak.moneytransfer.web.json

import arrow.core.Either
import com.squareup.moshi.Moshi
import com.squareup.moshi.Moshi.Builder
import pl.michalperlak.moneytransfer.core.util.of

class MoshiJsonMapper(
    private val moshi: Moshi = Builder().build()
) : JsonMapper {

    override fun <T : Any> read(value: String, targetClass: Class<T>): Either<Throwable, T> =
        Either.of {
            val adapter = moshi.adapter(targetClass)
            adapter.fromJson(value)
                ?: throw RuntimeException("Cannot extract ${targetClass.name} instance from value: $value")
        }

    override fun <T : Any> write(value: T): String {
        val adapter = moshi.adapter(value.javaClass)
        return adapter.toJson(value)
    }
}