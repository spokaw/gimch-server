// src/main/kotlin/com/diplom/Application.kt
package com.diplom

import com.diplom.config.AppConfig
import com.diplom.database.Database
import com.diplom.routes.authRoutes
import com.diplom.security.JwtConfig
import com.diplom.security.SecurityConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(
        factory = Netty,
        port = 80,
        host = "127.0.0.1",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val appConfig = AppConfig(environment.config)

    // Инициализация компонентов
    Database.init(appConfig.databaseConfig)
    JwtConfig.init(appConfig)

    // Настройка плагинов
    install(CallLogging) {
        level = Level.INFO
        filter { call -> !call.request.path().startsWith("/health") }
    }

    // Настройка безопасности
    SecurityConfig.configure(this, appConfig)

    // Маршрутизация
    routing {
        authRoutes()
    }
}