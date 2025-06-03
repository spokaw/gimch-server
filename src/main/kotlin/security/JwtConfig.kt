package com.diplom.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.diplom.config.AppConfig
import java.util.*

object JwtConfig {
    private const val TOKEN_VALIDITY_DAYS = 1
    private lateinit var config: AppConfig
    private lateinit var algorithm: Algorithm

    fun init(appConfig: AppConfig) {
        config = appConfig
        require(config.jwtSecret.length >= 64) {
            "JWT secret must be at least 64 characters long"
        }
        algorithm = Algorithm.HMAC256(config.jwtSecret)
    }

    fun generateToken(userId: String): String {
        return JWT.create()
            .withIssuer(config.jwtIssuer)
            .withAudience(config.jwtAudience)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + TOKEN_VALIDITY_DAYS * 24 * 3600 * 1000))
            .sign(algorithm)
    }
}