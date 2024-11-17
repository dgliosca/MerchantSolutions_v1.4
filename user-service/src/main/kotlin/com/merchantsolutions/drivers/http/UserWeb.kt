package com.merchantsolutions.drivers.http

import com.merchantsolutions.UserJson.auto
import com.merchantsolutions.application.UserHub
import com.merchantsolutions.domain.User
import com.merchantsolutions.domain.UserId
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Response.Companion.invoke
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun userApp(userHub: UserHub): RoutingHttpHandler {
    return routes(
        "/is-valid" bind GET to { Response(OK).with(Body.auto<Boolean>().toLens() of true) },
        "/user-by-token" bind GET to {
            Response(OK).with(
                Body.auto<User>().toLens() of User(UserId.of("3d02036f-4087-46e4-8a30-39d234d61de3"))
            )
        }
    )
}