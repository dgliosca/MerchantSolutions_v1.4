package com.merchantsolutions

import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import com.merchantsolutions.AuctionJson.auto

class AuctionServerTest {
    private val auctionServer: HttpHandler = routes("/register-product" bind POST to { Response(OK) })
    private val seller = Seller(auctionServer)

    class Seller(val client: HttpHandler) {

        fun registerProduct() {
            client(Request(POST, "/register-product").with(Product.lens of Product("candle-sticks")))
        }
        data class Product(val description: String) {
            companion object {
                val lens = Body.auto<Product>().toLens()
            }
        }
    }

    @Test
    fun `seller can register a new product`() {
        seller.registerProduct()
    }
}