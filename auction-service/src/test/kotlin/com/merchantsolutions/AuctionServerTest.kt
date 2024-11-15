package com.merchantsolutions

import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.drivers.http.auctionApp
import org.http4k.core.*
import org.junit.jupiter.api.Test
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isEmpty
import org.junit.jupiter.api.fail
import java.math.BigDecimal

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

    @Test
    fun `backoffice list products to start selling`() {
        seller.registerProduct(SellerActor.Product("Candle Sticks"))
        val products = backOffice.listProducts()

        assertThat(products.map { it.description }, hasElement("Candle Sticks"))
    }

    @Test
    fun `there is one auction to bid`() {
        seller.registerProduct(SellerActor.Product("Antique Vase"))
        val product = backOffice.listProducts()
            .find { it.description == "Antique Vase" } ?: fail("Couldn't find product")
        backOffice.startAuction(product.id)

        val auctionList = buyer.listAuctions()
        assertThat(auctionList, !isEmpty)
    }

    @Test
    fun `buyer can bid until auction closes`() {
        seller.registerProduct(SellerActor.Product("Antique Vase"))
        val product = backOffice.listProducts()
            .find { it.description == "Antique Vase" } ?: fail("Couldn't find product")
        backOffice.startAuction(product.id)

        val auction = buyer.listAuctions().first()
        buyer.placeABid(auction, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(product.id)
    }
}
