package com.example.services

import com.example.config.MAX_ORDER_DESCRIPTION_LENGTH
import com.example.config.MIN_ORDER_DESCRIPTION_LENGTH
import com.example.helpers.OrdersHelper
import com.example.models.CreateOrderRequest
import com.example.models.Order
import com.example.models.OrderResponse
import com.example.models.Orders
import org.dotenv.vault.dotenvVault
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import java.util.UUID

class OrderService {
    val dotenv = dotenvVault()

    private val database = Database.connect(
        url = "jdbc:postgresql://${dotenv["DB_HOST"]}:${dotenv["DB_PORT"]}/${dotenv["DB_NAME"]}",
        driver = "org.postgresql.Driver",
        user = dotenv["DB_USER"],
        password = dotenv["DB_PASSWORD"]
    )

    fun createOrder(orderRequest: CreateOrderRequest): Order? {
        // limits and description length checker
        if (!(OrdersHelper().amountLimitsChecker(orderRequest.amount))) return null
        if (!(OrdersHelper().descriptionLengthChecker(orderRequest.description))) return null

        val currentTimestamp = System.currentTimeMillis() / 1000
        val newOrder = Order {
            description = orderRequest.description
            amount = orderRequest.amount
            userId = orderRequest.userId
            createdTimestamp = currentTimestamp
        }

        database.sequenceOf(Orders).add(newOrder)
        val queryResult: Order? = database.sequenceOf(Orders).find { order -> order.createdTimestamp eq currentTimestamp }

        return queryResult
    }

    fun getOrderById(orderId: UUID): Order? {
//        if (!(OrdersHelper().orderUUIDChecker(orderId.toString()))) return null

        return database.sequenceOf(Orders).find { order -> order.id eq orderId }
    }

    fun getAllOrders(): Set<Order> = database.sequenceOf(Orders).toSet()

    fun deleteOrder(orderId: UUID): Boolean {
        val foundOrder = getOrderById(orderId)
        val queryResult = foundOrder?.delete()

        return queryResult == 1
    }
}