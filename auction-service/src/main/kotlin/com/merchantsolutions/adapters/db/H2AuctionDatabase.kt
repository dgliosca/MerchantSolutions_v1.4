package com.merchantsolutions.adapters.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class H2AuctionDatabase() : Storage {
    private val url = "jdbc:h2:mem:auction-database;DB_CLOSE_DELAY=-1"
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
    connection.createStatement().use { statement ->
        statement.execute("DROP ALL OBJECTS DELETE FILES")
        statement.execute(productsTable())
        statement.execute(auctionsTable())
        statement.execute(bidsTable())
    }
}

private fun productsTable(): String =
    """CREATE TABLE IF NOT EXISTS products (
        id UUID PRIMARY KEY,
        description VARCHAR(255),
        minimum_selling_price  DECIMAL(10, 2),
        currency VARCHAR(4)
       )"""

private fun auctionsTable(): String =
    """CREATE TABLE IF NOT EXISTS auctions (id UUID PRIMARY KEY, product_id UUID, state VARCHAR(100))"""

private fun bidsTable(): String =
    """CREATE TABLE IF NOT EXISTS bids (
    id IDENTITY PRIMARY KEY,
    user_id UUID,
    auction_id UUID,
    amount DECIMAL(10, 2),
    currency VARCHAR(4)
);"""