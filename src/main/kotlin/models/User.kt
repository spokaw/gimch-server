package com.diplom.models

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val id: Int,
    val phone: String?,
    val phoneVerified: Boolean,
    val googleId: String?,
    val passwordHash: String?,
    val displayName: String,
    val description: String?,
    val isBanned: Boolean,
    val gender: String?,
    val birthDate: LocalDate?,
    val premium: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

