package pl.michalperlak.moneytransfer.dto

import com.squareup.moshi.JsonClass
import pl.michalperlak.moneytransfer.core.domain.Currency

@JsonClass(generateAdapter = true)
data class NewAccountDto(
    val ownerId: String,
    val currency: Currency
)


