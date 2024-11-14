package com.merchantsolutions

import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test

class AuctionServerTest {
    private val auctionServer: HttpHandler = routes("/register-product" bind POST to { Response(OK) })
    private val seller = Seller(auctionServer)

    class Seller(val client: HttpHandler) {

        fun registerProduct() {
            client(Request(POST, "/register-product"))
        }
    }

    @Test
    fun `seller can register a new product`() {
        seller.registerProduct()
    }
}