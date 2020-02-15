package pl.michalperlak.moneytransfer.web.util

import arrow.core.MapK
import arrow.core.k
import arrow.core.updated
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues

fun jsonContentType(): MapK<CharSequence, CharSequence> =
        mapOf<CharSequence, CharSequence>(
                HttpHeaderNames.CONTENT_TYPE to HttpHeaderValues.APPLICATION_JSON
        ).k()

fun MapK<CharSequence, CharSequence>.location(uri: String) = updated(HttpHeaderNames.LOCATION, uri)