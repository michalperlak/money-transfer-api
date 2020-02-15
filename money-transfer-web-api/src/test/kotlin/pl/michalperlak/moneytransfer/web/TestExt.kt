package pl.michalperlak.moneytransfer.web

import arrow.core.getOrHandle
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import pl.michalperlak.moneytransfer.web.json.JsonMapper
import pl.michalperlak.moneytransfer.web.json.MoshiJsonMapper
import pl.michalperlak.moneytransfer.web.json.read

inline fun <reified T : Any> ExtractableResponse<Response>.extractBody(): T {
    val content = body().asString()
    val mapper: JsonMapper = MoshiJsonMapper()
    return mapper
            .read<T>(content)
            .getOrHandle { throw it }
}