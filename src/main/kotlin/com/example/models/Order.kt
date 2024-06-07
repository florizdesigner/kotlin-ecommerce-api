package com.example.models

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.util.UUID

interface Order: Entity<Order> {
    companion object: Entity.Factory<Order> ()

    val id: UUID?
    var description: String
    var amount: Int
    var userId: Int
    var createdTimestamp: Long?

    fun toOrderResponse(): OrderResponse? =
        this?.let { OrderResponse(it.id!!, it.description, it.amount, it.userId, it.createdTimestamp!!) }
}

object Orders: Table<Order>("orders") {
    val id = uuid("id").primaryKey().bindTo(Order::id)
    val description = varchar("description").bindTo(Order::description)
    val amount = int("amount").bindTo(Order::amount)
    val userId = int("user_id").bindTo(Order::userId)
    val createdTimestamp = long("created_timestamp").bindTo(Order::createdTimestamp)
}