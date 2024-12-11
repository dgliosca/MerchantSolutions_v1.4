package com.merchantsolutions.db

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.sql.DriverManager

class H2TransactorTest {
    private val url = "jdbc:h2:mem:auction-database;DB_CLOSE_DELAY=-1"
    private val user = "sa"
    private val password = ""

    private val connection = DriverManager.getConnection(url, user, password).apply {
        this.createStatement().executeUpdate("CREATE TABLE customers (id identity primary key, name varchar(255))")
        this.createStatement().executeUpdate("CREATE TABLE orders (id identity primary key, customer_id int)")
    }

    private val transactor = H2Transactor(connection)

    @Test
    fun `can commit a transaction`() {
        try {
            transactor {
                it.executeUpdate("insert into customers (name) values ('example')")

                it.executeUpdate("insert into customers (do_not_exist) values ('example')")

                it.executeUpdate("insert into orders (customer_id) values ('1')")
            }
        } catch (e: Exception) {
            // ignore
        }

        val result = transactor {
            it.executeQuery("select * from orders")
        }
        while (result.next()) {
            val actual = result.getString(1)
            assertThat(actual, equalTo(0))
        }
    }
}