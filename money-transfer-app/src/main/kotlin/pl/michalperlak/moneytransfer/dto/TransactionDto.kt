package pl.michalperlak.moneytransfer.dto

import com.squareup.moshi.JsonClass
import pl.michalperlak.moneytransfer.core.domain.TransactionType

@JsonClass(generateAdapter = true)
data class TransactionDto(
        val transactionId: String,
        val type: TransactionType,
        val amount: String,
        val destinationAccountId: String,
        val sourceAccountId: String? = null
)