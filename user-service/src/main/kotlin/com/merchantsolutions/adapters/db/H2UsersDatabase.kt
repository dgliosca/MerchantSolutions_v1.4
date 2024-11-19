package com.merchantsolutions.adapters.db

import com.merchantsolutions.adapters.Storage
import com.merchantsolutions.domain.UserId
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.UUID

class H2UsersDatabase() : Storage {
    private val url = "jdbc:h2:mem:users-database;DB_CLOSE_DELAY=-1"
    private val user = "sa"
    private val password = ""

    private val connection = DriverManager.getConnection(url, user, password).apply { setupDatabase(this) }

    override fun close() {
        statement.execute("SHUTDOWN")
        statement.close()
        connection.close()
    }

    override val statement: Statement = connection.createStatement()
}

fun setupDatabase(connection: Connection) {
    val buyerOne = UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
    val buyerTwo = UserId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
    val backOffice = UserId(UUID.fromString("00000000-0000-0000-0000-000000000003"))
    val seller = UserId(UUID.fromString("00000000-0000-0000-0000-000000000005"))
    connection.createStatement().use { statement ->
        statement.execute("DROP ALL OBJECTS DELETE FILES")
        statement.execute(usersTable())
        statement.execute(tokenToUserTable())

        statement.executeUpdate(addUser(buyerOne, "Bruce Wayne"))
        statement.executeUpdate(addUser(buyerTwo, "Tony Stark"))
        statement.executeUpdate(addUser(seller, "Vincent van Gogh"))
        statement.executeUpdate(addUser(backOffice, "Southebee"))
        statement.executeUpdate(addTokenFoUser(buyerOne, "00000000-0000-0000-1111-000000000001"))
        statement.executeUpdate(addTokenFoUser(buyerTwo, "00000000-0000-0000-2222-000000000002"))
        statement.executeUpdate(addTokenFoUser(seller, "00000000-0000-0000-3333-000000000003"))
        statement.executeUpdate(addTokenFoUser(backOffice, "00000000-0000-0000-5555-000000000005"))
    }
}

fun addUser(userId: UserId, fullName: String): String =
    "INSERT INTO users (id, full_name) VALUES ('${userId.value}', '$fullName');"

fun addTokenFoUser(userId: UserId, token: String): String =
    "INSERT INTO token_to_user (token, user_id) VALUES ('$token', '${userId.value}');"

fun tokenToUserTable(): String =
    """CREATE TABLE IF NOT EXISTS token_to_user(token UUID PRIMARY KEY, user_id UUID)"""

fun usersTable(): String =
    """CREATE TABLE IF NOT EXISTS users(id UUID PRIMARY KEY, full_name VARCHAR(255))"""
