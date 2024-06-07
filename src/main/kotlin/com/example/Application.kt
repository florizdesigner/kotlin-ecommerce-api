package com.example

import com.example.plugins.*
import com.example.routes.configureDocsRoute
import com.example.routes.configureOrdersRoutes
import com.example.routes.configureUsersRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        configureOrdersRoutes()
        configureSerialization()
        configureDocsRoute()
        configureUsersRoutes()
    }.start(wait = true)
}
