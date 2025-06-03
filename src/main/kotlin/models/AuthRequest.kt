package com.diplom.models

import kotlinx.serialization.Serializable

object AuthRequest {
    @Serializable
    data class Register(
        val phone: String,
        val password: String,
        val displayName: String
    )

    @Serializable
    data class Login(
        val phone: String,
        val password: String
    )
}