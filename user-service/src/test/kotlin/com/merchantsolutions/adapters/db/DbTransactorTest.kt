package com.merchantsolutions.adapters.db

import com.merchantsolutions.db.H2Transactor
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager

class DbTransactorTest {
    private val url = "jdbc:h2:mem:users-database;DB_CLOSE_DELAY=-1"
    private val user = "sa"
    private val password = ""

    private val connection: Connection = DriverManager.getConnection(url, user, password)
        .apply {
            autoCommit = false
            val statement = this.createStatement()
            statement.executeUpdate("""CREATE TABLE IF NOT EXISTS fruits(id VARCHAR(255) PRIMARY KEY, name VARCHAR(255))""")
        }

    @Test
    fun `can do a transaction`() {

        val transactor = H2Transactor(connection)

        transactor {
            it.executeUpdate("insert into fruits (id, name) values ('a', 'apple')")
        }
        val result = transactor {
            val result = it.executeQuery("select * from fruits")
            result.next()
            result.getString("name")
        }
        assertThat(result, equalTo("apple"))
    }

}

