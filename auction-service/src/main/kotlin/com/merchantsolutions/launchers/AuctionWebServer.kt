package com.merchantsolutions.launchers

import com.merchantsolutions.drivers.http.AuctionApi
import org.http4k.server.Undertow
import org.http4k.server.asServer

class AuctionWebServer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AuctionApi().asServer(Undertow()).start()
        }
    }
}
