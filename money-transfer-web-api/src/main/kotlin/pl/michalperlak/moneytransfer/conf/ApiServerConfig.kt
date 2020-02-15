package pl.michalperlak.moneytransfer.conf

import pl.michalperlak.moneytransfer.web.handler.AccountsHandler
import pl.michalperlak.moneytransfer.web.handler.ApiHandler
import pl.michalperlak.moneytransfer.web.json.JsonMapper
import pl.michalperlak.moneytransfer.web.json.MoshiJsonMapper

class ApiServerConfig private constructor(
        val port: Int,
        val handlers: Iterable<ApiHandler>
) {
    companion object {
        fun create(appConfig: AppConfig, jsonMapper: JsonMapper = MoshiJsonMapper(), port: Int = DEFAULT_PORT): ApiServerConfig {
            val handlers = createHandlers(appConfig, jsonMapper)
            return ApiServerConfig(port, handlers)
        }

        private fun createHandlers(appConfig: AppConfig, jsonMapper: JsonMapper): Iterable<ApiHandler> = listOf(
                AccountsHandler(
                        appConfig.accountsService,
                        jsonMapper
                )
        )

        const val DEFAULT_PORT = 9090
    }
}