package com.merchantsolutions.launchers

import com.merchantsolutions.drivers.http.UserApi
import org.http4k.server.Undertow
import org.http4k.server.asServer

class UserWebServer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            UserApi().asServer(Undertow()).start()
        }
    }
}
