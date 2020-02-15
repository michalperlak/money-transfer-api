package pl.michalperlak.moneytransfer.web

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import pl.michalperlak.moneytransfer.configureDefaultServer

internal class ServerStarterExtension : BeforeAllCallback, AfterAllCallback {
    private val server: ApiServer = configureDefaultServer()

    override fun beforeAll(context: ExtensionContext?) {
        val serverThread = Thread({ server.startAndBlock() }, "api-server-thread")
        serverThread.start()
    }

    override fun afterAll(context: ExtensionContext?) {
        server.stop()
    }
}