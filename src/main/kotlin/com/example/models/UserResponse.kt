package com.example.models

import com.example.helpers.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserResponse(
    val id: Int,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val email: String
)

@Serializable
data class UsersResponse(
    val status: String,
    val users: List<UserResponse?>
)

@Serializable
data class ApiSuccessResponse(
    val status: String,
    val user: UserResponse
)