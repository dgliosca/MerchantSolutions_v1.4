package com.merchantsolutions.drivers.http

import com.merchantsolutions.UserJson.auto
import com.merchantsolutions.UserJson.json
import com.merchantsolutions.adapters.InMemoryUsers
import com.merchantsolutions.application.UserHub
import com.merchantsolutions.domain.User
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Response.Companion.invoke
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun UserApi() = userApp(UserHub(InMemoryUsers()))

fun userApp(userHub: UserHub): RoutingHttpHandler {
    return routes(
        "/is-valid" bind GET to { request ->
            val token = request.json<String>()
            val result = userHub.isValid(token)
            Response(OK).with(Body.auto<Boolean>().toLens() of result)
        },
        "/user-by-token" bind GET to { request ->
            val token = request.json<String>()
            val result = userHub.getUserByToken(token)
            when (result) {
                null -> Response(NOT_FOUND)
                else -> Response(OK).with(Body.auto<User>().toLens() of result)
            }
        }
    )
}