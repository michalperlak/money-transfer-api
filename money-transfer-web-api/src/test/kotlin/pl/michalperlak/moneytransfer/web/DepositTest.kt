package pl.michalperlak.moneytransfer.web

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import pl.michalperlak.moneytransfer.conf.ApiServerConfig
import pl.michalperlak.moneytransfer.conf.ApiServerConfig.Companion.DEFAULT_PORT
import pl.michalperlak.moneytransfer.core.domain.Currency
import pl.michalperlak.moneytransfer.core.domain.Currency.PLN
import pl.michalperlak.moneytransfer.core.domain.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.web.handler.AccountsHandler
import pl.michalperlak.moneytransfer.web.handler.TransactionsHandler
import pl.michalperlak.moneytransfer.web.json.MoshiJsonMapper
import java.util.UUID

@ExtendWith(ServerStarterExtension::class)
internal class DepositTest {

    @Test
    fun `should return status 400 with error when transaction type not specified`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"amount": "100.00",
                	"destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(TransactionsHandler.CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return status 400 with error when destination account not specified`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "DEPOSIT",
                	"amount": "1000",
                }
            """.trimIndent())
        } When {
            post(TransactionsHandler.CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return status 400 with error when amount is invalid`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "DEPOSIT",
                	"amount": "-100.00",
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(TransactionsHandler.CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return status 400 with error when account id is invalid`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "DEPOSIT",
                	"amount": "100.00",
                    "destinationAccountId": "123456"
                }
            """.trimIndent())
        } When {
            post(TransactionsHandler.CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return status 404 when account with id not found`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "DEPOSIT",
                	"amount": "100.00",
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(TransactionsHandler.CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(404)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return status 200 with transaction details when executed successfully`() {
        val accountId = createAccount(validOwnerId())
        val transaction = Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "DEPOSIT",
                	"amount": "100.00",
                    "destinationAccountId": "$accountId"
                }
            """.trimIndent())
        } When {
            post(TransactionsHandler.CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(200)
        } Extract {
            extractBody<TransactionDto>()
        }

        assertAll(
                { assertEquals(DEPOSIT, transaction.type) },
                { assertEquals("100.00", transaction.amount) },
                { assertEquals(accountId, transaction.destinationAccountId) }
        )
    }

    private fun createAccount(ownerId: String, currency: Currency = PLN): String {
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

    private fun validOwnerId() = UUID.randomUUID().toString()
}