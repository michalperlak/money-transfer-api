package pl.michalperlak.moneytransfer.dto

import com.squareup.moshi.JsonClass
import pl.michalperlak.moneytransfer.core.domain.Currency

@JsonClass(generateAdapter = true)
data class AccountDto(
    val id: String,
    val currency: Currency,
    val ownerId: String,
    val balance: String
)