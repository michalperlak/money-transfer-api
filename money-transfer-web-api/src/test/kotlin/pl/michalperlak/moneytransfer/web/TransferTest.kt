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
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.dto.TransactionType.TRANSFER
import pl.michalperlak.moneytransfer.web.handler.TransactionsHandler.Companion.CREATE_TRANSACTION_PATH
import pl.michalperlak.moneytransfer.web.json.MoshiJsonMapper

@ExtendWith(ServerStarterExtension::class)
internal class TransferTest {

    @Test
    fun `should return 400 status with error when transaction type not specified`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"amount": "100.00",
                    "sourceAccountId": "b5213df1-9fc6-4e62-9d30-f0a2b4f96c74"
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 400 status with error when amount is invalid`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "TRANSFER",
                	"amount": "-100.00",
                    "sourceAccountId": "b5213df1-9fc6-4e62-9d30-f0a2b4f96c74"
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 400 status with error when source account id missing`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "TRANSFER",
                	"amount": "100.00",
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 400 status with error when source account does not exist`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "TRANSFER",
                	"amount": "100.00",
                    "sourceAccountId": "b5213df1-9fc6-4e62-9d30-f0a2b4f96c74"
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 400 status with error when dest account id is missing`() {
        val sourceAccountId = createAccount(validOwnerId())
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "TRANSFER",
                	"amount": "100.00",
                    "sourceAccountId": "$sourceAccountId"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 400 status with error when dest account does not exist`() {
        val sourceAccountId = createAccount(validOwnerId())
        Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "TRANSFER",
                	"amount": "100.00",
                    "sourceAccountId": "$sourceAccountId",
                    "destinationAccountId": "9203a2a2-cfb8-4ad1-a295-8f33f30ee366"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return status 200 with transaction details when executed successfully`() {
        // given
        val sourceAccountId = createAccount(validOwnerId())
        deposit(sourceAccountId, Money.of(1000))
        val destAccountId = createAccount(validOwnerId())
        val transaction = Given {
            port(DEFAULT_PORT)
            body("""
                {
                	"type": "TRANSFER",
                	"amount": "100.00",
                    "sourceAccountId": "$sourceAccountId",
                    "destinationAccountId": "$destAccountId"
                }
            """.trimIndent())
        } When {
            post(CREATE_TRANSACTION_PATH)
        } Then {
            statusCode(200)
        } Extract {
            extractBody<TransactionDto>()
        }

        assertAll(
                { assertEquals(TRANSFER, transaction.type) },
                { assertEquals("100.00", transaction.amount) },
                { assertEquals(sourceAccountId, transaction.sourceAccountId) },
                { assertEquals(destAccountId, transaction.destinationAccountId) }
        )
    }

    private fun deposit(accountId: String, amount: Money) {
        val depositTransaction = NewTransactionDto(
                type = DEPOSIT,
                amount = amount.asString(),
                destinationAccountId = accountId
        )
        val mapper = MoshiJsonMapper()
        Given {
            port(DEFAULT_PORT)
            body(mapper.write(depositTransaction))
        } When {
            post(CREATE_TRANSACTION_PATH)
        }
    }
}