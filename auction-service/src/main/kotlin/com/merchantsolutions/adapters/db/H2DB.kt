package com.merchantsolutions.adapters.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class H2DB() : Storage {
    val url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
    val user = "sa"
    val password = ""

    val connection = DriverManager.getConnection(url, user, password).apply { setupDatabase(this) }

    override fun close() {
        connection.close()
    }

    override val statement: Statement = connection.createStatement()
}

fun setupDatabase(connection: Connection) {
    connection.createStatement().use { statement ->
        statement.execute(
            """
            CREATE TABLE products (
                id UUID PRIMARY KEY,
                description VARCHAR(255),
                minimum_selling_price  DECIMAL(10, 2),
                currency VARCHAR(4)
            )
            """
        )
    }
}
