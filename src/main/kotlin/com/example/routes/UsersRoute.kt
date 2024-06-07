package com.example.routes

import com.example.helpers.ApiResponseStatuses
import com.example.models.*
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private fun User?.toUserResponse(): UserResponse? =
    this?.let { UserResponse(it.id!!, it.firstName, it.lastName, it.email) }
fun Application.configureUsersRoutes() {
    routing {
        route("/users") {
            val userService = UserService()
            createUser(userService)
            getUserById(userService)
            getAllUsers(userService)
            editUser(userService)
            deleteUser(userService)
        }
    }
}

fun Route.createUser(userService: UserService) {
    post {
        try {
            val requestBody = call.receive<CreateUserRequest>()

            userService.createUser(requestBody)
                ?.let { user -> user.toUserResponse() }
                ?.let { response -> call.respond(HttpStatusCode.Created, ApiSuccessResponse(
                    status = ApiResponseStatuses.SUCCESS.status,
                    user = response)) }
        } catch (e: IllegalArgumentException) {
            return@post call.respond(HttpStatusCode.BadRequest, ApiResponse(
                status = ApiResponseStatuses.FAILED.status,
                message = e.message.toString()))
        } catch (e: Exception) {
            return@post call.respond(HttpStatusCode.InternalServerError, ApiResponse(
                status = ApiResponseStatuses.ERROR.status, message = e.message.toString()))
        }
    }
}

fun Route.getUserById(userService: UserService) {
    get("{id}") {
        try {
            val id: Int = call.parameters["id"]!!.toInt()

            // TODO(): Отдавать exception если id != Int

            val queryResult = userService.getUserById(id)
                ?: throw IllegalArgumentException("User by id: $id is not found!")

            queryResult
                .let { user -> user.toUserResponse() }!!
                .let { response -> call.respond(HttpStatusCode.OK, ApiSuccessResponse(
                    status = ApiResponseStatuses.SUCCESS.status, user = response))}
        } catch (e: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest, ApiResponse(
                status = ApiResponseStatuses.FAILED.status, message = e.message.toString()))
        } catch (e: Exception) {
            return@get call.respond(HttpStatusCode.InternalServerError, ApiResponse(
                status = ApiResponseStatuses.ERROR.status, message = e.message.toString()))
        }
    }
}

fun Route.getAllUsers(userService: UserService) {
    get {
        try {
            val users = userService.getAllUsers().map(User::toUserResponse)
            call.respond(HttpStatusCode.OK, UsersResponse(
                status = ApiResponseStatuses.SUCCESS.status,
                users = users
            ))
        } catch (e: Exception) {
            return@get call.respond(HttpStatusCode.InternalServerError, ApiResponse(ApiResponseStatuses.ERROR.status, e.message.toString()))
        }
    }
}

fun Route.editUser(userService: UserService) {
    put("{id}") {
        try {
            val id = call.parameters["id"]!!.toInt()
            val request = call.receive<CreateUserRequest>()

            val updatedUser: User? = userService.editUser(id, request) ?: throw Exception("Unexpected error")

            updatedUser
                ?.let { user -> user.toUserResponse() }
                ?.let { response -> call.respond(HttpStatusCode.OK, ApiSuccessResponse(
                    status = ApiResponseStatuses.SUCCESS.status,
                    user = response
                )) }
        } catch (e: Exception) {
            return@put call.respond(HttpStatusCode.InternalServerError, ApiResponse(
                status = ApiResponseStatuses.ERROR.status,
                message = e.message.toString()
            ))
        }
    }
}
fun Route.deleteUser(userService: UserService) {
    delete("{id}") {
        try {
            val id: Int = call.parameters["id"]!!.toInt()

            userService.getUserById(id)
                ?: throw IllegalArgumentException("User by id: $id is not found!")

            val deleteResult = userService.deleteUser(id)

            if (deleteResult) {
                call.respond(HttpStatusCode.OK, ApiResponse(
                    status = ApiResponseStatuses.SUCCESS.status,
                    message = "User was successfully deleted"
                ))
            } else {
                call.respond(HttpStatusCode.BadRequest, ApiResponse(
                    status = ApiResponseStatuses.FAILED.status,
                    message = "Oops! The user was not deleted"
                ))
            }
        } catch (e: Exception) {
            return@delete call.respond(HttpStatusCode.InternalServerError, ApiResponse(
                status = ApiResponseStatuses.ERROR.status,
                message = e.message.toString()
            ))
        }
    }
}