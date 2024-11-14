package com.merchantsolutions

import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.drivers.http.auctionApp
import org.http4k.core.*
import org.junit.jupiter.api.Test
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement

class AuctionServerTest {
    private val auctionServer: HttpHandler = auctionApp(AuctionHub())

    private val seller = SellerActor(auctionServer)
    private val buyer = BuyerActor(auctionServer)
    private val backOffice = BackOfficeActor(auctionServer)

    @Test
    fun `seller can register a new product`() {
        seller.registerProduct(SellerActor.Product("candle-sticks"))
    }

    @Test
    fun `there are no auction to bid`() {
        val auctionList = buyer.listAuctions()
        assertThat(auctionList, equalTo(emptyList()))
    }

//    @Test
//    fun `there is one auction to bid`() {
//        seller.registerProduct()
//        backOffice.startAuction()
//
//        val auctionList = buyer.listAuctions()
//        assertThat(auctionList, !isEmpty)
//    }

    @Test
    fun `backoffice list products to start selling`() {
        seller.registerProduct(SellerActor.Product("Candle Sticks"))
        val products = backOffice.listProducts()

        assertThat(products.map { it.description }, hasElement("Candle Sticks"))
    }
}
