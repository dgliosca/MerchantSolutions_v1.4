@file:JvmName("UserWebServer")

package com.merchantsolutions.launchers

import com.merchantsolutions.drivers.http.UserApi
import org.http4k.server.Undertow
import org.http4k.server.asServer

fun main(args: Array<String>) {
    UserApi().asServer(Undertow(port = 8081)).start()
}


