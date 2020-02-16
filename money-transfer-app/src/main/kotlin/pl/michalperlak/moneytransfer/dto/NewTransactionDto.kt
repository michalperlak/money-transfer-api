package pl.michalperlak.moneytransfer.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewTransactionDto(
        val type: TransactionType,
        val amount: String,
        val destinationAccountId: String,
        val sourceAccountId: String? = null
)