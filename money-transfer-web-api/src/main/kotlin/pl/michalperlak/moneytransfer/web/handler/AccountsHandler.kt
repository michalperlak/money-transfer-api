package pl.michalperlak.moneytransfer.web.handler

import arrow.core.Option
import arrow.core.getOrHandle
import io.netty.handler.codec.http.HttpResponseStatus
import org.reactivestreams.Publisher
import pl.michalperlak.moneytransfer.app.AccountsService
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import pl.michalperlak.moneytransfer.web.dto.ErrorMessage
import pl.michalperlak.moneytransfer.web.json.JsonMapper
import pl.michalperlak.moneytransfer.web.json.read
import pl.michalperlak.moneytransfer.web.util.bodyToMono
import pl.michalperlak.moneytransfer.web.util.jsonContentType
import pl.michalperlak.moneytransfer.web.util.location
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes

class AccountsHandler(
        private val accountsService: AccountsService,
        private val jsonMapper: JsonMapper
) : ApiHandler {

    override fun register(routes: HttpServerRoutes): HttpServerRoutes {
        return routes
                .post(CREATE_ACCOUNT_PATH, this::createAccount)
    }

    private fun createAccount(request: HttpServerRequest, response: HttpServerResponse): Publisher<Void> {
        return request
                .bodyToMono { jsonMapper.read<NewAccountDto>(it) }
                .flatMap {
                    it
                            .map { newAccount -> createAccount(newAccount) }
                            .getOrHandle { error -> parseError(error) }
                }
                .flatMap { it.applyAndSend(response) }
    }

    private fun createAccount(newAccount: NewAccountDto): Mono<ResponseSpec> {
        return accountsService
                .createAccount(newAccount)
                .map {
                    it.map {  accountDto ->
                        ResponseSpec(
                                status = HttpResponseStatus.CREATED,
                                headers = jsonContentType().location("$BASE_ACCOUNTS_PATH/${accountDto.id}")
                        )
                    }
                }
                .getOrHandle {
                    Mono.just(
                            ResponseSpec(
                                    status = HttpResponseStatus.BAD_REQUEST,
                                    headers = jsonContentType(),
                                    body = Option.just(error(it.name))
                            )
                    )
                }
    }

    private fun parseError(throwable: Throwable): Mono<ResponseSpec> {
        return Mono.just(
                ResponseSpec(
                        status = HttpResponseStatus.BAD_REQUEST,
                        headers = jsonContentType(),
                        body = Option.just(
                                error("Error parsing creation request: ${throwable.message ?: "PARSE_ERROR"}")
                        )
                )
        )
    }

    private fun error(message: String): String =
            jsonMapper
                    .write(
                            ErrorMessage(message)
                    )

    companion object {
        private const val BASE_ACCOUNTS_PATH = "/api/accounts"
        const val CREATE_ACCOUNT_PATH = BASE_ACCOUNTS_PATH
    }
}