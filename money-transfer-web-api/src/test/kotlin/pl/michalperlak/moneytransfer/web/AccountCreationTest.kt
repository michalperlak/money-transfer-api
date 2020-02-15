package pl.michalperlak.moneytransfer.web

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.michalperlak.moneytransfer.conf.ApiServerConfig.Companion.DEFAULT_PORT
import pl.michalperlak.moneytransfer.web.handler.AccountsHandler

@ExtendWith(ServerStarterExtension::class)
internal class AccountCreationTest {

    @Test
    fun `should return 404 with error when currency is invalid`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
	                "ownerId": "69946ab4-5a7c-4f0b-b4fe-41aeda9ab17b",
                    "currency": "INVALID"
                }
            """.trimIndent())
        } When {
            post(AccountsHandler.CREATE_ACCOUNT_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 404 with error when owner id is invalid`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
	                "ownerId": "123456",
                    "currency": "PLN"
                }                
            """.trimIndent())
        } When {
            post(AccountsHandler.CREATE_ACCOUNT_PATH)
        } Then {
            statusCode(400)
            body("error", notNullValue())
        }
    }

    @Test
    fun `should return 201 with created account location when account successfully created`() {
        Given {
            port(DEFAULT_PORT)
            body("""
                {
	                "ownerId": "69946ab4-5a7c-4f0b-b4fe-41aeda9ab17b",
                    "currency": "PLN"
                }                
            """.trimIndent())
        } When {
            post(AccountsHandler.CREATE_ACCOUNT_PATH)
        } Then {
            statusCode(201)
            header("Location", containsString("/api/accounts"))
        }
    }
}