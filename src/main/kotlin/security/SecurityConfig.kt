package com.diplom.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.diplom.config.AppConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

object SecurityConfig {
    fun configure(application: Application, config: AppConfig) {
        application.install(Authentication) {
            jwt("auth-jwt") {
                realm = config.jwtRealm
                verifier(
                    JWT.require(Algorithm.HMAC256(config.jwtSecret))
                        .withAudience(config.jwtAudience)
                        .withIssuer(config.jwtIssuer)
                        .build()
                )
                validate { credential ->
                    credential.payload.getClaim("userId").asString()
                        .takeIf { it.isNotEmpty() }
                        ?.let { JWTPrincipal(credential.payload) }
                }
                challenge { _, _ ->
                    throw AuthenticationException("Invalid token")
                }
            }
        }
    }
}

class AuthenticationException(message: String) : Exception(message)