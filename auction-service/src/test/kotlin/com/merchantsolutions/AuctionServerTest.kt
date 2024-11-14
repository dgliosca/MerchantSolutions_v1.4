package com.merchantsolutions

import org.http4k.core.*
import org.junit.jupiter.api.Test
import com.merchantsolutions.AuctionJson.auto
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Method.GET

class AuctionServerTest {
    private val auctionServer: HttpHandler = auctionApp()

    private val seller = Seller(auctionServer)
    private val buyer = Buyer(auctionServer)

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

class Auction