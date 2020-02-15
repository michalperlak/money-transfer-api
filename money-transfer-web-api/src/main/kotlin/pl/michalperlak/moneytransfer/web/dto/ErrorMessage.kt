package pl.michalperlak.moneytransfer.web.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorMessage(val error: String)