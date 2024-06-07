package com.example.models

import com.example.helpers.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val description: String,
    val amount: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("created_timestamp")
    val createdTimestamp: Long
)

@Serializable
data class ApiSuccessOrderResponse(
    val status: String,
    val order: OrderResponse
)

@Serializable
data class ApiSuccessOrdersResponse(
    val status: String,
    val orders: List<OrderResponse?>
)