package com.example

import com.example.models.ApiResponse
import com.example.models.CreateOrderRequest
import com.example.models.Order
import com.example.services.OrderService
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.ktorm.dsl.eq
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OrderTest {
    private val orderService = OrderService()

    @Test()
    fun orderLifeCycleTest() {
        val orderRequest = CreateOrderRequest(
            description = "Test item for create order",
            amount = 30000,
            userId = 4
        )

        // Создаем заказ => полученный результат является Order
        val createdOrder = orderService.createOrder(orderRequest)
        assert(createdOrder is Order)

        // В полученном заказе id is UUID, а createdTimestamp is Long
        assertIs<UUID>(createdOrder!!.id)
        assertIs<Long>(createdOrder.createdTimestamp)

        // Получаем заказ по ID => createdOrder и checkedOrderById совпадают
        val checkedOrderById = orderService.getOrderById(createdOrder.id!!)
        assertEquals(createdOrder, checkedOrderById, "The created and received orders are not equal")

        // Выгружаем список всех ордеров => созданный заказ есть в этом списке
        val checkedOrderByAllOrders = orderService.getAllOrders()
        checkedOrderByAllOrders.find { order -> order.id == createdOrder.id }

        // Удаляем заказ => получаем в результате true = успех
        val deletedOrder = orderService.deleteOrder(createdOrder.id!!)
        assert(deletedOrder) // It must be true if the deletion was successful

        // Получаем заказ по ID => должен вернуться null
        val checkDeletedOrder = orderService.getOrderById(createdOrder.id!!)
        assertEquals(null, checkDeletedOrder)
    }

    @Test
    fun positiveCreateOrderTest() {
        val validInputData: List<CreateOrderRequest> = listOf(
            CreateOrderRequest(description = "Test item for create order", amount = 15000, userId = 5),
            CreateOrderRequest(description = "Тестовое описание тестового заказа, который нам пришел с сайта https://yandex.ru, после этого мы списались с клиентом в чате все", amount = 1, userId = 3),
            CreateOrderRequest(description = "T", amount = 350000, userId = 4)
        )

        for (request in validInputData) {
            val createdOrder = orderService.createOrder(request)

            assertIs<Order>(createdOrder) // createdOrder is Order
            assertIs<UUID>(createdOrder.id) // createdOrder.id is UUID
            assertIs<Long>(createdOrder.createdTimestamp) // createdOrder.createdTimestamp is Long

            // Проверка того, что данные при записи в БД не изменяются
            assertEquals(request.description, createdOrder.description)
            assertEquals(request.amount, createdOrder.amount)
            assertEquals(request.userId, createdOrder.userId)

            val checkOrder = orderService.getOrderById(createdOrder.id!!)
            assertEquals(createdOrder, checkOrder)
        }
    }

    @Test
    fun negativeCreateOrderTest() {
        val invalidInputData: List<CreateOrderRequest> = listOf(
            CreateOrderRequest(description = "", amount = 0, userId = 5),
            CreateOrderRequest(description = "", amount = 100, userId = 4),
            CreateOrderRequest(description = "test", amount = -1000, userId = 1),
            CreateOrderRequest(description = "Тестовое описание тестового заказа, который нам пришел с сайта https://yandex.ru, после этого мы списались с клиентом в чате все.", amount = 350001, userId = 5), // description = 129 symbols
            CreateOrderRequest(description = "Тестовое описание тестового заказа, который нам пришел с сайта https://yandex.ru, после этого мы списались с клиентом в чате все.", amount = 350001, userId = 100), // description = 129 symbols and not found userId
            )

        for (request in invalidInputData) {
            // везде должны получить как результат выполнения = null
            val result = orderService.createOrder(request)
            assertEquals(null, result)
        }
    }

    @Test
    fun positiveGetOrderById() {
        val validInputData = UUID.fromString("ac66505c-47bc-43bc-a578-5da03507b133")
        val result = orderService.getOrderById(validInputData)

        assert(result is Order)
        assertEquals(validInputData, result!!.id)
    }

    @Test
    fun negativeGetOrderById() {
        // TODO(): check last 2 cases (exception)

        val invalidInputData: List<UUID> = listOf(
            UUID.randomUUID(), // валидный uuid, но его нет в БД
            UUID.fromString("ac66505c-47bc-43bc-a578-5da03507b13"), // 35 symbols
            UUID.fromString("ac66505c-47bc-43bc-a578-5da03507b1331"), // 37 symbols
            UUID.fromString("test")
        )

        for (uuid in invalidInputData) {
            val result = orderService.getOrderById(uuid)
            assertEquals(null, result)
        }
    }
}