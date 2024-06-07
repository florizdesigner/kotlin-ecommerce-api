package com.example.models

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.util.UUID

interface User: Entity<User> {
    companion object: Entity.Factory<User> ()

    val id: Int?
    var firstName: String
    var lastName: String
    var email: String
}
object Users: Table<User>(
"users") {
    val id = int("id").primaryKey().bindTo(User::id)
    val firstName = varchar("first_name").bindTo(User::firstName)
    val lastName = varchar("last_name").bindTo(User::lastName)
    val email = varchar("email").bindTo(User::email)
}