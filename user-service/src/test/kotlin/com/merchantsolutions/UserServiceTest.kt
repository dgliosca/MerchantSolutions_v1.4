package com.merchantsolutions

import com.merchantsolutions.UserJson.auto
import com.merchantsolutions.UserJson.json
import com.merchantsolutions.adapters.db.H2Users
import com.merchantsolutions.adapters.db.H2UsersDatabase
import com.merchantsolutions.application.UserHub
import com.merchantsolutions.db.H2Transactor
import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.drivers.http.userApp
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Request.Companion.invoke
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private val storage = H2UsersDatabase()
    private val userHub = UserHub(H2Users(), H2Transactor(storage.connection))
    private val userService = userApp(userHub)

    @AfterAll
    fun afterAll() {
        storage.close()
    }

    @Test
    fun `is valid user`() {
        val response = userService(
            Request(GET, "/is-valid").with(
                Body.auto<String>().toLens() of "00000000-0000-0000-1111-000000000001"
            )
        )
        assertThat(response.json<Boolean>(), equalTo(true))
    }

    @Test
    fun `invalid token`() {
        val response = userService(
            Request(GET, "/is-valid").with(
                Body.auto<String>().toLens() of "00000000-0000-0000-0000-000000000009"
            )
        )
        assertThat(response.json<Boolean>(), equalTo(false))
    }

    @Test
    fun `user by token`() {
        val response = userService(
            Request(GET, "/user-by-token").with(
                Body.auto<String>().toLens() of "00000000-0000-0000-1111-000000000001"
            )
        )
        assertThat(response.json<User>(), equalTo(User(UserId.of("00000000-0000-0000-0000-000000000001"))))
    }

    @Test
    fun `user does not exist`() {
        val response = userService(
            Request(GET, "/user-by-token").with(
                Body.auto<String>().toLens() of "00000000-0000-0000-0000-000000000009"
            )
        )
        assertThat(response.status, equalTo(NOT_FOUND))
    }
}

