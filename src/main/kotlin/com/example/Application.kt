package com.example

import com.example.plugins.*
import com.example.routes.configureOrdersRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureOrdersRoutes()
        configureSerialization()
    }.start(wait = true)
}

//fun Application.module() {
//    configureHTTP()
//    configureSerialization()
//    configureDatabases()
//    configureRouting()
//}
