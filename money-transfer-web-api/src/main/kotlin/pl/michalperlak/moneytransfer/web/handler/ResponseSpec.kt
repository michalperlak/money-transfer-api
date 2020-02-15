package pl.michalperlak.moneytransfer.web.handler

import arrow.core.MapK
import arrow.core.Option
import arrow.core.extensions.mapk.align.empty
import arrow.core.getOrElse
import io.netty.handler.codec.http.HttpResponseStatus
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerResponse

data class ResponseSpec(
        val status: HttpResponseStatus = HttpResponseStatus.OK,
        val body: Option<String> = Option.empty(),
        val headers: MapK<CharSequence, CharSequence> = empty()
) {
    fun applyAndSend(response: HttpServerResponse): Mono<Void> {
        val withStatus = response.status(status)
        val withHeaders = headers
                .toList()
                .fold(withStatus) { resp, header -> resp.addHeader(header.first, header.second) }
        return body
                .map { content ->
                    withHeaders.sendString(Mono.just(content))
                            .then()
                }
                .getOrElse { withHeaders.send() }
    }
}