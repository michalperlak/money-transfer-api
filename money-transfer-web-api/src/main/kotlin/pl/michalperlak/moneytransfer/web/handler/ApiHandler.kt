package pl.michalperlak.moneytransfer.web.handler

import reactor.netty.http.server.HttpServerRoutes

interface ApiHandler {
    fun register(routes: HttpServerRoutes): HttpServerRoutes
}