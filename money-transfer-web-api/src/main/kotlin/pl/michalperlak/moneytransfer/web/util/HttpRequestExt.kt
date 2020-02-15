package pl.michalperlak.moneytransfer.web.util

import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServerRequest
import java.nio.charset.Charset

fun <T> HttpServerRequest.bodyToMono(charset: Charset = Charset.defaultCharset(), mapper: (String) -> T): Mono<T> =
    receive()
        .aggregate()
        .asString(charset)
        .map(mapper)