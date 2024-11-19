package com.merchantsolutions.adapters.db

import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class H2UsersTest {
    val storage = H2UsersDatabase()
    val h2Users = H2Users(storage.statement)

    @AfterAll
    fun afterAll() {
        storage.close()
    }

    @Test
    fun `can get a token for a user`() {
        val buyerOne = UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"))

        val user = h2Users.getUserByToken("00000000-0000-0000-1111-000000000001")
        assertThat(user, equalTo(User(buyerOne)))
    }

    @Test
    fun `is valid token`() {
        val actual = h2Users.isValid("00000000-0000-0000-1111-000000000001")
        assertThat(actual, equalTo(true))
    }
}