package pl.michalperlak.moneytransfer

import pl.michalperlak.moneytransfer.conf.ApiServerConfig
import pl.michalperlak.moneytransfer.conf.AppConfig
import pl.michalperlak.moneytransfer.web.ApiServer

fun configureDefaultServer(): ApiServer {
    val appConfig = AppConfig()
    val apiServerConfig = ApiServerConfig.create(appConfig)
    return ApiServer.create(apiServerConfig)
}

fun main() {
    configureDefaultServer().startAndBlock()
}