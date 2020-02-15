package pl.michalperlak.moneytransfer.web

import pl.michalperlak.moneytransfer.conf.ApiServerConfig
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer

class ApiServer private constructor(
        private val server: HttpServer
) {
    private lateinit var disposableServer: DisposableServer

    @Synchronized
    fun start(): DisposableServer =
            if (serverStarted()) {
                disposableServer
            } else {
                server.bindNow()
            }

    fun startAndBlock() {
        start()
                .onDispose()
                .block()
    }

    @Synchronized
    fun stop() {
        if (!serverStarted() || disposableServer.isDisposed) {
            return
        }
        disposableServer.dispose()
    }

    private fun serverStarted(): Boolean = ::disposableServer.isInitialized

    companion object {
        fun create(config: ApiServerConfig): ApiServer {
            val handlers = config.handlers
            val server = HttpServer
                    .create()
                    .port(config.port)
                    .route {
                        handlers.fold(it) { routes, handler -> handler.register(routes) }
                    }
            return ApiServer(server)
        }
    }
}