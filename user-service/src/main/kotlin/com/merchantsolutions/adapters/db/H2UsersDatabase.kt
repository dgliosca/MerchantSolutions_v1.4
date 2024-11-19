package com.merchantsolutions.adapters.db

import com.merchantsolutions.adapters.Storage
import com.merchantsolutions.domain.UserId
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.UUID

class H2UsersDatabase() : Storage {
    val url = "jdbc:h2:mem:users-database;DB_CLOSE_DELAY=-1"
    val user = "sa"
    val password = ""

    val connection = DriverManager.getConnection(url, user, password).apply { setupDatabase(this) }

    override fun close() {
        connection.close()
    }

    override val statement: Statement = connection.createStatement()
}

fun setupDatabase(connection: Connection) {
    val buyerOne = UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
    val buyerTwo = UserId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
    val seller = UserId(UUID.fromString("00000000-0000-0000-0000-000000000005"))
    val backOffice = UserId(UUID.fromString("00000000-0000-0000-0000-000000000003"))
    val tableCreations = connection.createStatement()
    tableCreations.execute(usersTable())
    tableCreations.execute(tokenToUserTable())

    val preCannedStatement = connection.createStatement()
    preCannedStatement.executeUpdate(addUser(buyerOne, "Bruce Wayne"))
    preCannedStatement.executeUpdate(addUser(buyerTwo, "Tony Stark"))
    preCannedStatement.executeUpdate(addUser(seller, "Vincent van Gogh"))
    preCannedStatement.executeUpdate(addUser(backOffice, "Southebee"))
    preCannedStatement.executeUpdate(addTokenFoUser(buyerOne, "00000000-0000-0000-0000-000000000001"))
    preCannedStatement.executeUpdate(addTokenFoUser(buyerTwo, "00000000-0000-0000-0000-000000000002"))
    preCannedStatement.executeUpdate(addTokenFoUser(seller, "00000000-0000-0000-0000-000000000003"))
    preCannedStatement.executeUpdate(addTokenFoUser(backOffice, "00000000-0000-0000-0000-000000000005"))
}

fun addUser(userId: UserId, fullName: String): String =
    "INSERT INTO users (id, full_name) VALUES ('${userId.value}', '$fullName');"

fun addTokenFoUser(userId: UserId, token: String): String =
    "INSERT INTO token_to_user (token, user_id) VALUES ('$token', '${userId.value}');"

fun tokenToUserTable(): String =
    """CREATE TABLE IF NOT EXISTS token_to_user(token UUID PRIMARY KEY, user_id UUID)"""

fun usersTable(): String =
    """CREATE TABLE IF NOT EXISTS users(id UUID PRIMARY KEY, full_name VARCHAR(255))"""
