package pl.michalperlak.moneytransfer.web

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import pl.michalperlak.moneytransfer.conf.ApiServerConfig.Companion.DEFAULT_PORT
import pl.michalperlak.moneytransfer.core.domain.Currency
import pl.michalperlak.moneytransfer.core.domain.Currency.PLN
import pl.michalperlak.moneytransfer.dto.AccountDto
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import pl.michalperlak.moneytransfer.web.handler.AccountsHandler
import pl.michalperlak.moneytransfer.web.json.MoshiJsonMapper
import java.util.UUID

@ExtendWith(ServerStarterExtension::class)
internal class AccountRetrievalTest {

    @Test
    fun `should return 404 status when account with id not found`() {
        Given {
            port(DEFAULT_PORT)
        } When {
            get("/api/accounts/12345")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `should return 200 with account details when account exists`() {
        val ownerId = validOwnerId()
        val currency = PLN
        val path = createAccount(ownerId, currency)
        val accountDto =
                Given {
                    port(DEFAULT_PORT)
                } When {
                    get(path)
                } Then {
                    statusCode(200)
                } Extract {
                    extractBody<AccountDto>()
                }
        assertAll(
                { assertEquals(ownerId, accountDto.ownerId) },
                { assertEquals(currency, accountDto.currency) },
                { assertEquals("0.00", accountDto.balance) }
        )
    }

    private fun createAccount(ownerId: String, currency: Currency = PLN): String {
        val newAccountDto = NewAccountDto(ownerId = ownerId, currency = currency)
        val mapper = MoshiJsonMapper()
        return Given {
            port(DEFAULT_PORT)
            body(mapper.write(newAccountDto))
        } When {
            post(AccountsHandler.CREATE_ACCOUNT_PATH)
        } Extract {
            header("Location")
        }
    }

    private fun validOwnerId() = UUID.randomUUID().toString()
}