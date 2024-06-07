package com.example.services

import arrow.fx.coroutines.Use
import com.example.models.CreateUserRequest
import com.example.models.User
import com.example.models.Users
import org.dotenv.vault.dotenvVault
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import kotlin.reflect.typeOf

class UserService {
    val dotenv = dotenvVault()

    private val database = Database.connect(
        url = "jdbc:postgresql://${dotenv["DB_HOST"]}:${dotenv["DB_PORT"]}/${dotenv["DB_NAME"]}",
        driver = "org.postgresql.Driver",
        user = dotenv["DB_USER"],
        password = dotenv["DB_PASSWORD"]
    )

    private fun getUserByEmail(email: String): Boolean {
        val queryResult = database.sequenceOf(Users).find { user -> user.email eq email }
        return queryResult is User
    }

    fun createUser(userRequest: CreateUserRequest): User? {
        val createdUser = User {
            firstName = userRequest.firstName
            lastName = userRequest.lastName
            email = userRequest.email
        }

        val userIsExist = getUserByEmail(userRequest.email)
        if (userIsExist) throw IllegalArgumentException("This user.email is exist!")

        database.sequenceOf(Users).add(createdUser)
        val queryResult = database.sequenceOf(Users).find { user -> user.email eq createdUser.email }

        return queryResult
    }

    fun getUserById(id: Int): User? = database.sequenceOf(Users).find { user -> user.id eq id }

    fun getAllUsers(): List<User> = database.sequenceOf(Users).toList()

    fun editUser(id: Int, createUserRequest: CreateUserRequest): User? {
        val foundUser = getUserById(id)
        foundUser?.firstName = createUserRequest.firstName
        foundUser?.lastName = createUserRequest.lastName
        foundUser?.email = createUserRequest.email

        if (getUserByEmail(createUserRequest.email)) throw Exception("This email already exist!")

        val res = foundUser?.flushChanges()

        return if (res == 1) {
            getUserById(id)
        } else {
            null
        }
    }

    fun deleteUser(id: Int): Boolean {
        val user = getUserById(id)
        val queryResult = user?.delete()

        return queryResult == 1
    }
}