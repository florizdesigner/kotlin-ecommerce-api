package com.example.routes

import com.example.models.*
import com.example.services.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import java.util.UUID

private fun Order?.toOrderResponse(): OrderResponse? =
    this?.let { OrderResponse(it.id!!, it.description, it.amount, it.userId, it.createdTimestamp!!) }

fun Application.configureOrdersRoutes() {
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/orders")
        }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }

    routing {
        route("/orders") {
            val orderService = OrderService()
            createOrder(orderService)
            getOrderById(orderService)
            getAllOrders(orderService)
            deleteOrder(orderService)
        }
    }
}


fun Route.createOrder(orderService: OrderService) {
    post {
        try {
            val request = call.receive<CreateOrderRequest>()

            call.application.environment.log.info("Started request")
            orderService.createOrder(orderRequest = request)
                ?.let { createdOrder -> createdOrder.toOrderResponse() }
                ?.let { response -> call.respond(HttpStatusCode.Created, response)}
                ?: return@post call.respond(HttpStatusCode.BadRequest, ApiResponse("failed","Can't create order"))
        } catch (e: Exception) {
            print(e)
            throw e
        }
    }
}

fun Route.getOrderById(orderService: OrderService) {
    get("{id}") {
        try {
            val id: String = call.parameters["id"]!!
            if (id.length != 36) throw IllegalArgumentException("The number of characters should be 36")

            orderService.getOrderById(UUID.fromString(id))
                ?.let { order -> order.toOrderResponse() }
                ?.let { response -> call.respond(HttpStatusCode.OK, response) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, ApiResponse("failed","Order id is not found"))
        } catch (e: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest, ApiResponse("failed", e.message.toString()))
        } catch (e: Exception) {
            print(e)
            return@get call.respond(HttpStatusCode.BadRequest, ApiResponse("error", e.toString()))
        }
    }
}

fun Route.getAllOrders(orderService: OrderService) {
    get {
        val orders = orderService.getAllOrders()
            .map(Order::toOrderResponse)

        call.respond(message = OrdersResponse("success", orders))
    }
}

fun Route.deleteOrder(orderService: OrderService) {
    delete("{id}") {
        try {
            val id: String = call.parameters["id"]!!
            if (id.length != 36) throw IllegalArgumentException("The number of characters should be 36")

            val deleteQueryResult = orderService.deleteOrder(UUID.fromString(id))

            if (deleteQueryResult) {
                call.respond(HttpStatusCode.OK, ApiResponse("success", "order successfully deleted"))
            } else {
                call.respond(HttpStatusCode.BadRequest, ApiResponse("failed", "order is not deleted"))
            }
        } catch (e: IllegalArgumentException) {
            return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse("failed", e.message.toString()))
        } catch (e: Exception) {
            return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse("error", ""))
        }
    }
}