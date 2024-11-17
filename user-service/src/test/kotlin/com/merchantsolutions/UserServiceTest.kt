package com.merchantsolutions

import com.merchantsolutions.UserJson.auto
import com.merchantsolutions.UserJson.json
import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Request.Companion.invoke
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test

class UserServiceTest {
    private val userService = userApp()

    @Test
    fun `is valid user`() {
        val response = userService(Request(GET, "/is-valid").with(Body.auto<String>().toLens() of "123"))
        assertThat(response.json<Boolean>(), equalTo(true))
    }

    @Test
    fun `user by token`() {
        val response = userService(Request(GET, "/user-by-token").with(Body.auto<String>().toLens() of "123"))
        assertThat(response.json<User>(), equalTo(User(UserId.of("3d02036f-4087-46e4-8a30-39d234d61de3"))))
    }
}

fun userApp(): RoutingHttpHandler {
    return routes(
        "/is-valid" bind GET to { Response(OK).with(Body.auto<Boolean>().toLens() of true) },
        "/user-by-token" bind GET to {
            Response(OK).with(
                Body.auto<User>().toLens() of User(UserId.of("3d02036f-4087-46e4-8a30-39d234d61de3"))
            )
        }
    )
}