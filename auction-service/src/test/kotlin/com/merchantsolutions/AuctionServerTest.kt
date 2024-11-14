package com.merchantsolutions

import org.http4k.core.*
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import com.merchantsolutions.AuctionJson.auto
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET
import org.http4k.routing.RoutingHttpHandler

class AuctionServerTest {
    private val auctionServer: HttpHandler = auctionApp()

    private val seller = Seller(auctionServer)
    private val buyer = Buyer(auctionServer)

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

    class Buyer(val client: HttpHandler) {
        fun listAuctions() = activeAuctions(client(Request(GET, "/active-auctions")))

        private val activeAuctions = Body.auto<List<Auction>>().toLens()
    }

    @Test
    fun `seller can register a new product`() {
        seller.registerProduct()
    }

    @Test
    fun `there are no auction to bid`() {
        val auctionList = buyer.listAuctions()
        assertThat(auctionList, equalTo(emptyList()))
    }
}

fun auctionApp(): RoutingHttpHandler {
    return routes(
        "/register-product" bind POST to { Response(OK) },
        "/active-auctions" bind GET to { Response(OK).with(AuctionResult.lens of listOf()) }
    )
}

class Auction
class AuctionResult {
    companion object {
        val lens = Body.auto<List<Auction>>().toLens()
    }

}
