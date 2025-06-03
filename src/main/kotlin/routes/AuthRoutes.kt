package com.diplom.routes

import com.diplom.models.AuthRequest
import com.diplom.security.JwtConfig
import com.diplom.security.PasswordHasher
import com.diplom.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun Route.authRoutes() {
    route("/auth") {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        post("/register") {
            try {
                val request = call.receive<AuthRequest.Register>()

                // Проверка существующего пользователя
                UserService.findByPhone(request.phone)?.let {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "User already exists"))
                    return@post
                }

                // Создание пользователя
                val user = UserService.createUser(
                    phone = request.phone,
                    googleId = null,
                    password = request.password,
                    displayName = request.displayName
                )

                // Генерация токена
                val token = JwtConfig.generateToken(user.id.toString())
                call.respond(HttpStatusCode.Created, mapOf("token" to token))

            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Server error"))
            }
        }

        post("/login") {
            try {
                val request = call.receive<AuthRequest.Login>()

                // Поиск пользователя
                val user = UserService.findByPhone(request.phone) ?: run {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                    return@post
                }

                // Проверка пароля
                if (user.passwordHash == null ||
                    !PasswordHasher.verify(request.password, user.passwordHash)) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                    return@post
                }

                // Генерация токена
                val token = JwtConfig.generateToken(user.id.toString())
                call.respond(mapOf("token" to token))

            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Server error"))
            }
        }
    }
}