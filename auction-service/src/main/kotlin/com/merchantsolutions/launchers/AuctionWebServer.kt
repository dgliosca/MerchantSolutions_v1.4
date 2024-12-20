@file:JvmName("AuctionWebServer")

package com.merchantsolutions.launchers

import com.merchantsolutions.drivers.http.AuctionApi
import org.http4k.server.Undertow
import org.http4k.server.asServer


fun main(args: Array<String>) {
    AuctionApi().asServer(Undertow(port = 8080)).start()
}
