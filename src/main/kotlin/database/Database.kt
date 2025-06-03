package com.diplom.database

import com.diplom.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object Database {
    private lateinit var dataSource: HikariDataSource

    fun init(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.url
            username = config.user
            password = config.password
            maximumPoolSize = config.maxPoolSize
            connectionTimeout = 30000
            validationTimeout = 5000
            leakDetectionThreshold = 60000
            driverClassName = "org.postgresql.Driver"
        }
        dataSource = HikariDataSource(hikariConfig)
    }

    suspend fun <T> withConnection(block: (Connection) -> T): T {
        return dataSource.connection.use(block)
    }

    suspend fun <T> withTransaction(block: (Connection) -> T): T {
        return dataSource.connection.use { conn ->
            conn.autoCommit = false
            try {
                val result = block(conn)
                conn.commit()
                result
            } catch (e: Exception) {
                conn.rollback()
                throw e
            }
        }
    }
}