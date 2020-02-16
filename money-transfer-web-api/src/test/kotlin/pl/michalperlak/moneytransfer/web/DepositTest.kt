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
import pl.michalperlak.moneytransfer.conf.ApiServerConfig.Companion.DEFAULT_PORT
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.web.handler.TransactionsHandler

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
    fun `should return status 400 with error when account with id not found`() {
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
            statusCode(400)
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
}