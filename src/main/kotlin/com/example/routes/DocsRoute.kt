package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*

fun Application.configureDocsRoute() {
    routing {
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
//            codegen = StaticHtmlCodegen()
        }
    }
}