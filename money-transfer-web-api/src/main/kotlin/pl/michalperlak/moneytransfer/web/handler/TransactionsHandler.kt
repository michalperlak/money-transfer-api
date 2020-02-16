package pl.michalperlak.moneytransfer.web.handler

import arrow.core.Option.Companion.just
import arrow.core.getOrHandle
import io.netty.handler.codec.http.HttpResponseStatus
import pl.michalperlak.moneytransfer.app.TransactionsService
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.web.dto.ErrorMessage
import pl.michalperlak.moneytransfer.web.json.JsonMapper
import pl.michalperlak.moneytransfer.web.json.read
import pl.michalperlak.moneytransfer.web.util.bodyToMono
import pl.michalperlak.moneytransfer.web.util.jsonContentType
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import reactor.netty.http.server.HttpServerRoutes

class TransactionsHandler(
        private val transactionsService: TransactionsService,
        private val jsonMapper: JsonMapper
) : ApiHandler {
    override fun register(routes: HttpServerRoutes): HttpServerRoutes {
        return routes
                .post(CREATE_TRANSACTION_PATH, this::createTransaction)
    }

    private fun createTransaction(request: HttpServerRequest, response: HttpServerResponse): Mono<Void> {
        return request
                .bodyToMono { jsonMapper.read<NewTransactionDto>(it) }
                .flatMap {
                    it
                            .map { transaction -> executeTransaction(transaction) }
                            .getOrHandle { error -> parseError(error) }
                }
                .flatMap { it.applyAndSend(response) }
    }

    private fun parseError(error: Throwable): Mono<ResponseSpec> {
        return Mono.just(
                ResponseSpec(
                        status = HttpResponseStatus.BAD_REQUEST,
                        headers = jsonContentType(),
                        body = just(error("Error parsing transaction request: ${error.message}"))
                )
        )
    }

    private fun executeTransaction(transaction: NewTransactionDto): Mono<ResponseSpec> {
        return transactionsService
                .execute(transaction)
                .map {
                    it
                            .map { transactionDto ->
                                ResponseSpec(
                                        status = HttpResponseStatus.OK,
                                        headers = jsonContentType(),
                                        body = just(jsonMapper.write(transactionDto))
                                )
                            }.getOrHandle { error ->
                                ResponseSpec(
                                        status = HttpResponseStatus.BAD_REQUEST,
                                        headers = jsonContentType(),
                                        body = just(error(error.message))
                                )
                            }

                }
    }

    private fun error(message: String): String =
            jsonMapper
                    .write(
                            ErrorMessage(message)
                    )

    companion object {
        const val CREATE_TRANSACTION_PATH = "/api/transactions"
    }
}