package pl.michalperlak.moneytransfer.web

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import pl.michalperlak.moneytransfer.conf.ApiServerConfig
import pl.michalperlak.moneytransfer.core.domain.Currency
import pl.michalperlak.moneytransfer.core.domain.Currency.PLN
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import pl.michalperlak.moneytransfer.web.handler.AccountsHandler
import pl.michalperlak.moneytransfer.web.json.MoshiJsonMapper
import java.util.UUID

fun createAccount(ownerId: String, currency: Currency = PLN): String {
    val newAccountDto = NewAccountDto(ownerId = ownerId, currency = currency)
    val mapper = MoshiJsonMapper()
    val location = Given {
        port(ApiServerConfig.DEFAULT_PORT)
        body(mapper.write(newAccountDto))
    } When {
        post(AccountsHandler.CREATE_ACCOUNT_PATH)
    } Extract {
        header("Location")
    }
    return location.substringAfter("/api/accounts/")
}

fun validOwnerId() = UUID.randomUUID().toString()