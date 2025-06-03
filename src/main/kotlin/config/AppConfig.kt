package com.diplom.config

import io.ktor.server.config.*
import io.github.cdimascio.dotenv.dotenv
import java.io.File

class AppConfig(config: ApplicationConfig) {
    private val dotenv = run {
        val envFile = File(".env").takeIf { it.exists() }
            ?: File("gimch-server/.env").takeIf { it.exists() }
            ?: throw IllegalStateException(".env file not found in project root or gimch-server folder")

        dotenv {
            ignoreIfMissing = false
            directory = envFile.parent ?: "."
        }
    }

    val jwtSecret = dotenv["JWT_SECRET"] ?: throw IllegalStateException("JWT_SECRET must be set in .env")
    val jwtIssuer = dotenv["JWT_ISSUER"]
    val jwtAudience = dotenv["JWT_AUDIENCE"]
    val jwtRealm = dotenv["JWT_REALM"]

    val databaseConfig = DatabaseConfig(
        url = "jdbc:postgresql://localhost:5432/${dotenv["DB_NAME"]}",
        user = dotenv["DB_USER"],
        password = dotenv["DB_PASSWORD"],
        maxPoolSize = dotenv["DB_POOL_SIZE"]?.toIntOrNull() ?: 10
    )
}

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int
)