package com.merchantsolutions.drivers.http

import com.merchantsolutions.UserJson.auto
import com.merchantsolutions.UserJson.json
import com.merchantsolutions.adapters.db.H2Users
import com.merchantsolutions.adapters.db.H2UsersDatabase
import com.merchantsolutions.application.UserHub
import com.merchantsolutions.db.H2Transactor
import com.merchantsolutions.domain.User
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Response.Companion.invoke
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun UserApi(): RoutingHttpHandler {
    val storage = H2UsersDatabase()
    val users = H2Users()
    val transactor = H2Transactor(storage.connection)
    return ServerFilters.CatchAll()
        .then(userApp(UserHub(users, transactor)))
}

fun <TX> userApp(userHub: UserHub<TX>): RoutingHttpHandler {
    return routes(
        "/is-valid" bind GET to { request ->
            val token = request.json<String>()
            val result = userHub.isValid(token)
            Response(OK).with(Body.auto<Boolean>().toLens() of result)
        },
        "/user-by-token" bind GET to { request ->
            val token = request.json<String>()
            when (val result = userHub.getUserByToken(token)) {
                null -> Response(NOT_FOUND)
                else -> Response(OK).with(Body.auto<User>().toLens() of result)
            }
        }
    )
}
