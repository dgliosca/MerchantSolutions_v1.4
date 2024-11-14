package com.merchantsolutions

import org.http4k.core.*
import org.junit.jupiter.api.Test
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo

class AuctionServerTest {
    private val auctionServer: HttpHandler = auctionApp()

    private val seller = SellerActor(auctionServer)
    private val buyer = Buyer(auctionServer)

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