package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest (
    val description: String,
    val amount: Int,
    @SerialName("user_id")
    val userId: Int
)