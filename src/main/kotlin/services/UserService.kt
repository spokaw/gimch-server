package com.diplom.services

import com.diplom.database.Database
import com.diplom.models.User
import com.diplom.security.PasswordHasher
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime

object UserService {
    suspend fun createUser(
        phone: String?,
        googleId: String?,
        password: String?,
        displayName: String
    ): User {
        val passwordHash = password?.let { PasswordHasher.hash(it) }

        return Database.withTransaction { conn ->
            val stmt = conn.prepareStatement(
                """
                INSERT INTO users (
                    phone, google_id, password_hash, display_name
                ) VALUES (?, ?, ?, ?)
                RETURNING *
                """
            )

            stmt.setString(1, phone)
            stmt.setString(2, googleId)
            stmt.setString(3, passwordHash)
            stmt.setString(4, displayName)

            val rs = stmt.executeQuery()
            if (rs.next()) toUser(rs) else throw Exception("User creation failed")
        }
    }

    suspend fun findByPhone(phone: String): User? {
        return Database.withConnection { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM users WHERE phone = ?")
            stmt.setString(1, phone)

            val rs = stmt.executeQuery()
            if (rs.next()) toUser(rs) else null
        }
    }

    private fun toUser(rs: ResultSet): User {
        return User(
            id = rs.getInt("id"),
            phone = rs.getString("phone"),
            phoneVerified = rs.getBoolean("phone_verified"),
            googleId = rs.getString("google_id"),
            passwordHash = rs.getString("password_hash"),
            displayName = rs.getString("display_name"),
            description = rs.getString("description"),
            isBanned = rs.getBoolean("is_banned"),
            gender = rs.getString("gender"),
            birthDate = rs.getDate("birth_date")?.toLocalDate(),
            premium = rs.getBoolean("premium"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }
}